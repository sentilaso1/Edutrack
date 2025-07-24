package com.example.edutrack.curriculum.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "Title", length = 255, unique = true)
    private String title;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    public Tag() {
    }

    public Tag(Integer id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }


    // Getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String toString() {
        return "Tags {id=" + id + ", title=" + title + ", description=" + description + "}";
    }
}
