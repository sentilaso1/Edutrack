package com.example.edutrack.curriculum.model;

import com.example.edutrack.profiles.model.CV;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "cv_courses")
public class CVCourse {

    @EmbeddedId
    private CVCourseId id = new CVCourseId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cvId")
    @JoinColumn(name = "cv_id", nullable = false)
    private CV cv;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "added_date", nullable = false, updatable = false)
    @CreatedDate
    private Date addedDate = new Date();

    public CVCourse() {
    }

    public CVCourse(CV cv, Course course) {
        this.cv = cv;
        this.course = course;
        this.id = new CVCourseId(cv.getId(), course.getId());
    }

    public CVCourseId getId() {
        return id;
    }

    public void setId(CVCourseId id) {
        this.id = id;
    }

    public CV getCv() {
        return cv;
    }

    public void setCv(CV cv) {
        this.cv = cv;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        this.addedDate = addedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CVCourse that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CVCourse{" +
                "cvId=" + id.getCvId() +
                ", courseId=" + id.getCourseId() +
                ", addedDate=" + addedDate +
                '}';
    }
}
