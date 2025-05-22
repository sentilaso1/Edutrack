package com.example.edutrack.curriculum.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

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
    private Date uploadDate = new Date();

    public TeachingMaterials() {}
    public TeachingMaterials(byte[] file, Course course) {
        this.file = file;
        this.course = course;
    }

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

    public Date getUploadDate() {
        return uploadDate;
    }
    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String toString() {
        return "TeachingMaterials {id=" + id + ", file=" + file + ", course=" + course + ", uploadDate=" + uploadDate + "}";
    }
}
