package com.example.edutrack.curriculum.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = Boolean.FALSE;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = Boolean.FALSE;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate = LocalDateTime.now();

    public Course(){}
    public Course(String name, String description, Boolean isOpen, Boolean isOpen1) {
        this.name = name;
        this.description = description;
        this.isOpen = isOpen;
        this.isOpen = isOpen1;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<TeachingMaterials> materials = new ArrayList<>();

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isOpen=" + isOpen +
                ", isOpen=" + isOpen +
                ", createdDate=" + createdDate +
                '}';
    }
}
