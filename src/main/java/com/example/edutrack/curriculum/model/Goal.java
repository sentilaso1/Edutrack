package com.example.edutrack.curriculum.model;

import com.example.edutrack.accounts.model.Mentee;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "mentee_id")
    private Mentee mentee;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        TODO, IN_PROGRESS, DONE
    }

    public Goal() {}

    public Goal(Mentee mentee, String title, String description, LocalDate targetDate, Status status) {
        this.mentee = mentee;
        this.title = title;
        this.description = description;
        this.targetDate = targetDate;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public Mentee getMentee() {
        return mentee;
    }
    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDate getTargetDate() {
        return targetDate;
    }
    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}
