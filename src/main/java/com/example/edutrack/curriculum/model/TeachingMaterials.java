package com.example.edutrack.curriculum.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TEACHING_MATERIALS")
public class TeachingMaterials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    private byte[] file;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate = LocalDateTime.now();

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public byte[] getFile() {
        return file;
    }
    public void setFile(byte[] file) {
        this.file = file;
    }
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }


}
