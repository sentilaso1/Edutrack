package com.example.edutrack.curriculum.model;

import com.example.edutrack.accounts.model.Mentor;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

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

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = Boolean.FALSE;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<TeachingMaterial> materials = new ArrayList<>();

    public Course() {}

    public Course(String name, String description, Boolean isOpen) {
        this.name = name;
        this.description = description;
        this.isOpen = isOpen;
    }

    // Getters and Setters

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

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public List<TeachingMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<TeachingMaterial> materials) {
        this.materials = materials;
    }

    @OneToMany(mappedBy = "course")
    private List<CourseMentor> applications;

    public List<CourseMentor> getApplications() {
        return applications;
    }
    public void setApplications(List<CourseMentor> applications) {
        this.applications = applications;
    }


    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isOpen=" + isOpen +
                ", createdDate=" + createdDate +
                '}';
    }
}
