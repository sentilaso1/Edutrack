package com.example.edutrack.profiles.dto;

import java.util.UUID;

public class CourseApplicationDetail {
    private UUID courseId;
    private String description;

    // Getters and setters
    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
