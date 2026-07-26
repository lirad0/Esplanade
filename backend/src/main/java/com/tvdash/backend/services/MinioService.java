package com.tvdash.backend.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tvdash.backend.config.MinioProperties;
import com.tvdash.backend.exceptions.MSUnableToGetFileTypeException;
import com.tvdash.backend.exceptions.MSUnableToPutObjectException;
import com.tvdash.backend.exceptions.MSUnsupportedMediaException;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MinioService {

    private static Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png", "image/svg+xml", "image/jpeg");

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final FileService fileService;

    @Value("${minio.bucket}")
    private String bucket;

    public String insertImage(InputStream image, long fileSize) throws MSUnableToGetFileTypeException, MSUnsupportedMediaException, MSUnableToPutObjectException {
        byte[] imageBytes;

        try {
            imageBytes = image.readAllBytes();
        } catch (IOException e) {
            throw new MSUnableToGetFileTypeException();
        }

        String contentType;

        try {
            contentType = fileService.getFileType(imageBytes);
        } catch (IOException e) {
            throw new MSUnableToGetFileTypeException();
        }

        String imageName;

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new MSUnsupportedMediaException();
        }

        String extension = resolveExtension(contentType);

        imageName = UUID.randomUUID() + extension;

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucket())
                            .object(imageName)
                            .stream(new ByteArrayInputStream(imageBytes), imageBytes.length, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            throw new MSUnableToPutObjectException();
        }

        return imageName;
    }

    // Streaming the image piece by piece
    public InputStream getImageStream(String imageName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(imageName)
                        .build());
    }

    public String getContentType(String objectName) throws Exception {
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build());

        return stat.contentType(); // e.g. "image/png"
    }

    private String resolveExtension(String contentType) {
        return switch (contentType) {
            case "image/png" ->
                ".png";
            case "image/jpeg" ->
                ".jpg";
            case "image/svg+xml" ->
                ".svg";
            default ->
                "";
        };
    }
}
