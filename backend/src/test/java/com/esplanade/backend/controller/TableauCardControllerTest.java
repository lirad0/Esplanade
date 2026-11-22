package com.esplanade.backend.controller;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.esplanade.backend.controller.TableauCardController;
import com.esplanade.backend.model.TableauCardData;
import com.esplanade.backend.repository.TableauCardRepository;
import com.esplanade.backend.services.MinioService;
import com.esplanade.backend.services.NetworkService;
import org.springframework.mock.web.MockMultipartFile;

@WebMvcTest(TableauCardController.class)
@AutoConfigureMockMvc(addFilters = false)
class TableauCardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TableauCardRepository repository;

    @MockBean
    private MinioService minioService;

    @MockBean
    private NetworkService networkService;

    @Test
    void shouldRejectJsonCardCreation() throws Exception {
        mockMvc.perform(post("/api/tableau/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sample\",\"imageName\":\"sample.png\",\"url\":\"https://example.com\"}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldUploadImageAndSaveTheGeneratedimageName() throws Exception {
        TableauCardData card = new TableauCardData();
        card.setName("Sample");
        card.setImageName("generated-image.png");
        card.setUrl("https://example.com");

        when(minioService.insertImage(any(), anyLong())).thenReturn("generated-image.png");
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
