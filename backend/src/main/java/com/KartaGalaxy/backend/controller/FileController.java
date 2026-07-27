package com.KartaGalaxy.backend.controller;

import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.KartaGalaxy.backend.services.MinioService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*")
public class FileController {

    private final MinioService minioService;

    @GetMapping("/{fileName}")
    public ResponseEntity<InputStreamResource> streamImage(@PathVariable String fileName) {
        InputStream stream;
        String type;

        try {
            stream = minioService.getImageStream(fileName);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        try {
            type = minioService.getContentType(fileName);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        
        boolean typeCheckPassed = !(type == null && type.isEmpty() && type.isBlank());

        if (typeCheckPassed) {
            MediaType contentType;

            try {
                contentType = MediaType.parseMediaType(type);
            } catch (InvalidMediaTypeException e) { // 
                return ResponseEntity.internalServerError().body(null);
            }

            return ResponseEntity.ok()
                    .contentType(contentType)
                    .body(new InputStreamResource(stream));
        }

        return ResponseEntity.notFound().build();
    }
}
