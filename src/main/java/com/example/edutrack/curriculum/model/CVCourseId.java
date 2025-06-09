package com.example.edutrack.curriculum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CVCourseId implements Serializable {

    @Column(name = "cv_id", columnDefinition = "BINARY(16)")
    private UUID cvId;

    @Column(name = "course_id", columnDefinition = "BINARY(16)")
    private UUID courseId;

    public CVCourseId() {
    }

    public CVCourseId(UUID cvId, UUID courseId) {
        this.cvId = cvId;
        this.courseId = courseId;
    }

    public UUID getCvId() {
        return cvId;
    }

    public void setCvId(UUID cvId) {
        this.cvId = cvId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CVCourseId that)) return false;
        return Objects.equals(cvId, that.cvId) &&
                Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cvId, courseId);
    }
}
