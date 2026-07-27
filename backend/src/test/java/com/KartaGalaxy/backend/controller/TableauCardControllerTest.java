package com.kartagalaxy.backend.controller;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kartagalaxy.backend.config.MinioProperties;
import com.kartagalaxy.backend.model.TableauCardData;
import com.kartagalaxy.backend.repository.TableauCardRepository;

import io.minio.MinioClient;

@WebMvcTest(TableauCardController.class)
class TableauCardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TableauCardRepository repository;

    @MockBean
    private MinioClient minioClient;

    @MockBean
    private MinioProperties minioProperties;

    @Test
    void shouldCreateCard() throws Exception {
        TableauCardData card = new TableauCardData();
        card.setName("Sample");
        card.setImageName("sample.png");
        card.setUrl("https://example.com");

        when(repository.save(any(TableauCardData.class))).thenReturn(card);

        mockMvc.perform(post("/api/tableau/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sample\",\"imageName\":\"sample.png\",\"url\":\"https://example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sample"));
    }

    @Test
    void shouldUploadImageAndSaveTheGeneratedimageName() throws Exception {
        TableauCardData card = new TableauCardData();
        card.setName("Sample");
        card.setImageName("generated-image.png");
        card.setUrl("https://example.com");

        when(minioProperties.getBucket()).thenReturn("images");
        doReturn(null).when(minioClient).putObject(any());
        when(repository.save(any(TableauCardData.class))).thenReturn(card);

        mockMvc.perform(multipart("/api/tableau/cards")
                        .file(new MockMultipartFile("file", "sample.png", MediaType.IMAGE_PNG_VALUE, "image-data".getBytes(StandardCharsets.UTF_8)))
                        .param("name", "Sample")
                        .param("url", "https://example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sample"))
                .andExpect(jsonPath("$.imageName").isNotEmpty());
    }
}
