package com.example.edutrack.curriculum.dto;

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
}
