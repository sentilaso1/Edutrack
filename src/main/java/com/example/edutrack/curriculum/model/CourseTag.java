package com.example.edutrack.curriculum.model;

import jakarta.persistence.*;
@Entity
@Table(name = "course_tags")
public class CourseTag {
    @EmbeddedId
    private CourseTagId id;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tags tag;

    public CourseTag(){}

    public CourseTag(Course course, Tags tag) {
        this.course = course;
        this.tag = tag;
        this.id = new CourseTagId(course.getId(), tag.getId());
    }

    public CourseTagId getId() {
        return id;
    }

    public void setId(CourseTagId id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Tags getTag() {
        return tag;
    }

    public void setTag(Tags tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "CourseTag {" + "id=" + id + ", course=" + course + ", tag=" + tag + '}';
    }
}
