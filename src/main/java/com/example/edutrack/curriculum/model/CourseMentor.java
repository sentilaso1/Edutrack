package com.example.edutrack.curriculum.model;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.common.model.CustomFormatter;
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

    @Column(name = "price", nullable = false)
    private Double price = 0.0;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(name = "syllabus", columnDefinition = "TEXT")
    private String syllabus;

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

    public Double getPrice() {
        return price;
    }

    public String getPriceFormatted() {
        return CustomFormatter.formatNumberWithSpaces(getPrice());
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    @Override
    public String toString() {
        return "CourseMentor{" +
                "id=" + id +
                ", mentor=" + mentor +
                ", course=" + course +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", syllabus='" + syllabus + '\'' +
                ", appliedDate=" + appliedDate +
                ", status=" + status +
                '}';
    }
}
