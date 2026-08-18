package com.esplanade.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.esplanade.backend.entity.TableauCard;
import com.esplanade.backend.exceptions.MSUnableToDeleteException;
import com.esplanade.backend.exceptions.MSUnableToGetFileTypeException;
import com.esplanade.backend.exceptions.MSUnableToPutObjectException;
import com.esplanade.backend.exceptions.MSUnsupportedMediaException;
import com.esplanade.backend.model.TableauCardData;
import com.esplanade.backend.repository.TableauCardRepository;
import com.esplanade.backend.services.MinioService;
import com.esplanade.backend.services.NetworkService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tableau/cards")
@CrossOrigin(origins = "*")
public class TableauCardController {
    private final TableauCardRepository repository;
    private final MinioService minioService;
    private final NetworkService networkService;

    @GetMapping
    public List<TableauCard> findAll() {
        List<TableauCard> ltcs = repository.findAll().stream().map((card) -> {
            TableauCard tc = new TableauCard();

            tc.setId(card.getId());
            tc.setImageUrl("http://" + networkService.getContainerExternalIP() + ":8001/file/" + card.getImageName());
            tc.setName(card.getName());
            tc.setUrl(card.getUrl());

            return tc;
        }).toList();

        return ltcs;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> uploadCardImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("url") String url) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        TableauCardData card = new TableauCardData();

        try (InputStream fs = file.getInputStream()) {
            try {
                String imageName = minioService.insertImage(fs, file.getSize());

                card.setImageName(imageName);
            } catch (MSUnsupportedMediaException e) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Only PNG, JPG and SVG files are allowed.");
            } catch (MSUnableToGetFileTypeException | MSUnableToPutObjectException ex) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("The file couldn't get processed.");
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("The file has a problem.");
        }

        card.setName(name);
        card.setUrl(url);

        return ResponseEntity.ok(repository.save(card));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableauCardData> findById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> update(@PathVariable String id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("url") String url) {
        Optional<TableauCardData> grabbedRepoItem = repository.findById(id);

        if (file == null || file.isEmpty()) {
            return grabbedRepoItem
                    .map(existing -> {
                        existing.setName(name);
                        existing.setUrl(url);
                        return ResponseEntity.ok(repository.save(existing));
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            String oldImageName = grabbedRepoItem.get().getImageName();

            try {
                minioService.deleteImage(oldImageName);
            } catch (MSUnableToDeleteException e) {
                // we just accept the brutal truth and go on with our life
            }
            
            try (InputStream fs = file.getInputStream()) {
                try {
                    String imageName = minioService.insertImage(fs, file.getSize());

                    return grabbedRepoItem
                            .map(existing -> {
                                existing.setName(name);
                                existing.setImageName(imageName);
                                existing.setUrl(url);
                                return ResponseEntity.ok(repository.save(existing));
                            })
                            .orElseGet(() -> ResponseEntity.notFound().build());
                } catch (MSUnsupportedMediaException e) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("Only PNG, JPG and SVG files are allowed.");
                } catch (MSUnableToGetFileTypeException | MSUnableToPutObjectException ex) {
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                            .body("The file couldn't get processed.");
                }

            } catch (IOException e) {
                return ResponseEntity.internalServerError().body("The file has a problem.");
            }
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        Optional<TableauCardData> grabbedRepoItem = repository.findById(id);
        String imageName = grabbedRepoItem.get().getImageName();

        try {
            minioService.deleteImage(imageName);
        } catch (MSUnableToDeleteException e) {
            // we just accept the brutal truth and go on with our life
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
