package com.tvdash.backend.controller;

import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tvdash.backend.services.MinioService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FileController {

    private final MinioService minioService;

    @GetMapping("/stream/{fileName}")
    public ResponseEntity<InputStreamResource> streamImage(@PathVariable String fileName) throws Exception {
        InputStream stream;

        try {
            stream = minioService.getImageStream(fileName);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        String type = minioService.getContentType(fileName);

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
