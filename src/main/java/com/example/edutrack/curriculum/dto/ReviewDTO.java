package com.example.edutrack.curriculum.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private String courseTitle;
    private String mentorName;
    private String content;
    private Double rating;
    private LocalDateTime createdDate;

    public ReviewDTO(String courseTitle, String mentorName, String content, Double rating, LocalDateTime createdDate) {
        this.courseTitle = courseTitle;
        this.mentorName = mentorName;
        this.content = content;
        this.rating = rating;
        this.createdDate = createdDate;
    }

    public String getCourseTitle() { return courseTitle; }
    public String getMentorName() { return mentorName; }
    public String getContent() { return content; }
    public Double getRating() { return rating; }
    public LocalDateTime getCreatedDate() { return createdDate; }
}