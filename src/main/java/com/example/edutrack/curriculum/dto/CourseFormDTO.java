package com.example.edutrack.curriculum.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Valid
public class CourseFormDTO {
    @NotBlank(message = "Course name can not be empty")
    @Size(min = 3, max = 100, message = "Course name should have at least from 3 to 100 characters")
    private String name;

    @Size(max = 500, message = "Description can not exceed 500 characters")
    private String description;

    @NotEmpty(message = "Should have at lease 1 tag")
    @Size(max = 10, message = "Max 10 tags")
    private List<@NotBlank(message = "Tag can not be empty")
    @Size(min = 2, max = 30, message = "Tag should be at lease from 2 to 30 characters") String> tagTexts;

    public CourseFormDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTagTexts() { return tagTexts; }
    public void setTagTexts(List<String> tagTexts) { this.tagTexts = tagTexts; }

    @Override
    public String toString() {
        return "CourseFormDTO {Name=" + name + ", description=" + description + ", tagTexts=" + tagTexts + "}";
    }
}