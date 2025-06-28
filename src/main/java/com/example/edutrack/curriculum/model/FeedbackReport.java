package com.example.edutrack.curriculum.model;

import com.example.edutrack.accounts.model.Mentee;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedback_reports")
public class FeedbackReport {

    public enum Status {
        PENDING,
        REVIEWED,
        DISMISSED
    }

    public enum Category {
        SPAM,
        OFFENSIVE,
        HARASSMENT,
        MISINFORMATION,
        OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "feedback_id", nullable = false)
    private Feedback feedback;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private Mentee reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category; // Violation category

    @Column(nullable = false, length = 512)
    private String reason; // Free-text description

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    public FeedbackReport() {}

    public FeedbackReport(Feedback feedback, Mentee reporter, Category category, String reason) {
        this.feedback = feedback;
        this.reporter = reporter;
        this.category = category;
        this.reason = reason;
        this.status = Status.PENDING;
    }

    // Getters and setters...
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Feedback getFeedback() { return feedback; }
    public void setFeedback(Feedback feedback) { this.feedback = feedback; }

    public Mentee getReporter() { return reporter; }
    public void setReporter(Mentee reporter) { this.reporter = reporter; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}