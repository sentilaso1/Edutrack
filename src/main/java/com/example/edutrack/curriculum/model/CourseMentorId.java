package com.example.edutrack.curriculum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public class CourseMentorId implements Serializable {
    @Column(name = "course_id")
    private UUID courseId;

    @Column(name = "mentor_id")
    private UUID mentorId;

    public CourseMentorId() {
    }

    public CourseMentorId(UUID courseId, UUID mentorId) {
        this.courseId = courseId;
        this.mentorId = mentorId;
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

    @Override
    public String toString() {
        return "CourseMentorId{" +
                "courseId=" + courseId +
                ", mentorId=" + mentorId +
                '}';
    }
}
