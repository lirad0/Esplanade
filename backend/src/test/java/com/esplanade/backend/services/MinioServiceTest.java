package com.esplanade.backend.services;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.esplanade.backend.config.MinioProperties;
import com.esplanade.backend.services.FileService;
import com.esplanade.backend.services.MinioService;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties minioProperties;

    @Mock
    private FileService fileService;

    private MinioService minioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        minioService = new MinioService(minioClient, minioProperties, fileService);
    }

    @Test
    void shouldUploadOriginalBytesToMinioAfterDetectingContentType() throws Exception {
        byte[] imageBytes = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };

        when(fileService.getFileType(any(byte[].class))).thenReturn("image/png");
        when(minioProperties.getBucket()).thenReturn("images");
        doReturn(null).when(minioClient).putObject(any(PutObjectArgs.class));

        minioService.insertImage(new ByteArrayInputStream(imageBytes), imageBytes.length);

        ArgumentCaptor<PutObjectArgs> captor = ArgumentCaptor.forClass(PutObjectArgs.class);
        verify(minioClient).putObject(captor.capture());

        assertThat(captor.getValue().stream().readAllBytes()).isEqualTo(imageBytes);
    }
}
