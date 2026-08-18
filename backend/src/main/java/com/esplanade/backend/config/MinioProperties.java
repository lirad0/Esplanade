package com.esplanade.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint;
    private int port;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private boolean secure;
}