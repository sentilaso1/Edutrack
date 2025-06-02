package com.example.edutrack.curriculum.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Valid
public class CourseFormDTO {
    @NotBlank(message = "Tên khóa học không được để trống")
    @Size(min = 3, max = 100, message = "Tên khóa học phải từ 3 đến 100 ký tự")
    private String name;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;

    @NotEmpty(message = "Phải có ít nhất 1 tag")
    @Size(max = 10, message = "Không được vượt quá 10 tags")
    private List<@NotBlank(message = "Tag không được để trống")
    @Size(min = 2, max = 30, message = "Tag phải từ 2 đến 30 ký tự") String> tagTexts;

    @NotEmpty(message = "Phải upload ít nhất 1 tài liệu")
    @Size(min = 1, max = 5, message = "Số lượng file từ 1 đến 5")
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