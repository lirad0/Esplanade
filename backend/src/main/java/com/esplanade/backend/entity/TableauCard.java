package com.esplanade.backend.entity;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TableauCard {
    @Id
    private String id;

    @NotBlank
    private String name;

    private String imageUrl;

    @NotBlank
    private String url;
}
