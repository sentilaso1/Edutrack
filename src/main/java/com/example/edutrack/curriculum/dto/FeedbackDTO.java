package com.example.edutrack.curriculum.dto;

import java.util.UUID;

public class FeedbackDTO {
    private String content;
    private Double rating;
    private boolean isAnonymous;
    private UUID menteeId;
    private UUID courseId;
    private UUID mentorId;

    public FeedbackDTO() {
    }

    public FeedbackDTO(String content, Double rating, UUID menteeId, UUID courseId, UUID mentorId) {
        this.content = content;
        this.rating = rating;
        this.menteeId = menteeId;
        this.courseId = courseId;
        this.mentorId = mentorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public boolean isAnonymous() { return isAnonymous; }

    public void setIsAnonymous(boolean isAnonymous) { this.isAnonymous = isAnonymous; }

    public UUID getMenteeId() {
        return menteeId;
    }

    public void setMenteeId(UUID menteeId) {
        this.menteeId = menteeId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public UUID getMentorId() {
        return mentorId;
    }

    public void setMentorId(UUID mentorId) {
        this.mentorId = mentorId;
    }
}
