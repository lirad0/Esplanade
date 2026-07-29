package com.kartagalaxy.backend.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {

    @Autowired
    private MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint(), minioProperties.getPort(), minioProperties.isSecure())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    // Ensures the configured bucket exists on startup so uploads don't fail on a fresh MinIO instance.
    @Bean
    public Object ensureBucketExists(MinioClient minioClient) throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(minioProperties.getBucket()).build()
        );
        
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(minioProperties.getBucket()).build());
        }

        return new Object();
    }
}