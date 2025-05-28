package com.example.edutrack.curriculum.model;

import jakarta.persistence.Column;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class CourseMentorId implements Serializable {
    @Column(name = "course_id")
    private UUID courseId;

    @Column(name = "mentor_id")
    private UUID mentorId;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseMentorId)) return false;
        CourseMentorId that = (CourseMentorId) o;
        return Objects.equals(courseId, that.courseId) &&
               Objects.equals(mentorId, that.mentorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, mentorId);
    }

    @Override
    public String toString() {
        return "CourseMentorID {courseId=" + courseId + ", mentorId=" + mentorId + "}";
    }
}
