package com.example.edutrack.curriculum.model;

import com.example.edutrack.accounts.model.Mentor;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class CourseMentor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Mentor mentor;

    @ManyToOne
    private Course course;

    @Column(name = "applied_date")
    private LocalDateTime appliedDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    public CourseMentor(UUID id, Mentor mentor, Course course, LocalDateTime appliedDate, ApplicationStatus status) {
        this.id = id;
        this.mentor = mentor;
        this.course = course;
        this.appliedDate = appliedDate;
        this.status = status;
    }

    public CourseMentor() {}


    public Mentor getMentor() {
        return mentor;
    }
    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
    public LocalDateTime getAppliedDate() {
        return appliedDate;
    }
    public void setAppliedDate(LocalDateTime appliedDate) {
        this.appliedDate = appliedDate;
    }
    public ApplicationStatus getStatus() {
        return status;
    }
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public String toString() {
        return "MentorApplication [id=" + id + ", mentor=" + mentor + ", course=" + course + ", appliedDate=" + appliedDate + ", status=" + status + "]";
    }
}
