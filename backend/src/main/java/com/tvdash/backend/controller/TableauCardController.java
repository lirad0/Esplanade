package com.tvdash.backend.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tvdash.backend.exceptions.MSUnableToGetFileTypeException;
import com.tvdash.backend.exceptions.MSUnableToPutObjectException;
import com.tvdash.backend.exceptions.MSUnsupportedMediaException;
import com.tvdash.backend.model.TableauCard;
import com.tvdash.backend.repository.TableauCardRepository;
import com.tvdash.backend.services.MinioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tableau/cards")
@CrossOrigin(origins = "*")
public class TableauCardController {
    private final TableauCardRepository repository;
    private final MinioService minioService;

    @GetMapping
    public List<TableauCard> findAll() {
        return repository.findAll();
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> uploadCardImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "url", required = false) String url) {
                
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty.");
        }

        TableauCard card = new TableauCard();

        try (InputStream fs = file.getInputStream()) {
            
            try  {
                String imageName = minioService.insertImage(fs, file.getSize());

                card.setImageName(imageName);
            } catch (MSUnsupportedMediaException e) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("Only PNG, JPG and SVG files are allowed.");
            } catch (MSUnableToGetFileTypeException | MSUnableToPutObjectException ex) {
                System.getLogger(TableauCardController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("The file has a problem.");
        }
    
        card.setName(name);
        card.setUrl(url);

        return ResponseEntity.ok(repository.save(card));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableauCard> findById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableauCard> update(@PathVariable String id, @Valid @RequestBody TableauCard card) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(card.getName());
                    existing.setImageName(card.getImageName());
                    existing.setUrl(card.getUrl());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
