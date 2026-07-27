package com.KartaGalaxy.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;

@Document(collection = "tableau_cards")
public class TableauCardData {
    @Id
    private String id;

    @NotBlank
    private String name;

    private String imageName;

    @NotBlank
    private String url;

    public TableauCardData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
