package com.esplanade.backend.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tableau_cards")
public class TableauCardData {
    @Id
    @Column(name = "id", nullable = false, updatable = false, length = 36)
    private String id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_name")
    private String imageName;

    @NotBlank
    @Column(name = "url", nullable = false)
    private String url;

    public TableauCardData() {
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
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
