package com.example.edutrack.curriculum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class CourseTagId implements Serializable {

    @Column(name = "course_id")
    private UUID courseId;

    @Column(name = "tag_id")
    private Integer tagId;

    public CourseTagId() {}

    public CourseTagId(UUID courseId, Integer tagId) {
        this.courseId = courseId;
        this.tagId = tagId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CourseTagId)) return false;
        CourseTagId that = (CourseTagId) o;
        return Objects.equals(courseId, that.courseId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, tagId);
    }

    @Override
    public String toString() {
        return "CourseTagID {courseId=" + courseId + ", tagId=" + tagId + "}";
    }
}
