package com.example.edutrack.curriculum.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class CourseFormDTO {
    private String name;
    private String description;
    private List<String> tagTexts;

    private MultipartFile[] files;

    public CourseFormDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTagTexts() { return tagTexts; }
    public void setTagTexts(List<String> tagTexts) { this.tagTexts = tagTexts; }

    public MultipartFile[] getFiles() { return files; }
    public void setFiles(MultipartFile[] files) { this.files = files; }

    @Override
    public String toString() {
        return "CourseFormDTO {Name=" + name + ", description=" + description + ", tagTexts=" + tagTexts + "}";
    }
}