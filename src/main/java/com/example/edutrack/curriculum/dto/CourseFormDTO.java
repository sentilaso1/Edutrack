package com.example.edutrack.curriculum.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class CourseFormDTO {
    private String name;
    private String description;
    private List<Integer> tagIds;
    private MultipartFile[] files;

    public CourseFormDTO() {

    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public List<Integer> getTagIds() {
        return tagIds;
    }
    public void setTagIds(List<Integer> tagIds) {

    }
    public MultipartFile[] getFiles() {
        return files;
    }

}
