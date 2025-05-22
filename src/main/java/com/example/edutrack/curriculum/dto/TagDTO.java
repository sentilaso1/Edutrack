package com.example.edutrack.curriculum.dto;

import java.security.PublicKey;

public class TagDTO {
    private String title;
    private String description;

    public TagDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "TagDTO {title=" + title + ", description=" + description + "}";
    }
}
