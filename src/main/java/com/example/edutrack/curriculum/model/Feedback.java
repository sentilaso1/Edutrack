package com.example.edutrack.curriculum.model;

import com.example.edutrack.accounts.model.Mentee;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    public enum Status {
        ACTIVE,
        HIDDEN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(columnDefinition = "DECIMAL(2,1)", nullable = false)
    private Double rating;

    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous = false;

    @ManyToOne
    @JoinColumn(name = "mentee_id")
    private Mentee mentee;

    @ManyToOne
    @JoinColumn(name = "course_mentor_id")
    private CourseMentor courseMentor;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    public Feedback() {}

    public Feedback(String content, Double rating, boolean isAnonymous, Mentee mentee, CourseMentor courseMentor) {
        this.content = content;
        this.rating = rating;
        this.isAnonymous = isAnonymous;
        this.mentee = mentee;
        this.courseMentor = courseMentor;
        this.status = Status.ACTIVE;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public boolean isAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }

    public CourseMentor getCourseMentor() {
        return courseMentor;
    }

    public void setCourseMentor(CourseMentor courseMentor) {
        this.courseMentor = courseMentor;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
