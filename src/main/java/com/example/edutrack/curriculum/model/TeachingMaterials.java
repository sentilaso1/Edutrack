package com.example.edutrack.curriculum.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "TEACHING_MATERIALS")
public class TeachingMaterials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(name = "file", columnDefinition = "LONGBLOB")
    private byte[] file;

    private String name;
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "upload_date")
    private Date uploadDate = new Date();

    public TeachingMaterials() {}
    public TeachingMaterials(String name, String fileType, byte[] file, Course course) {
        this.name = name;
        this.fileType = fileType;
        this.file = file;
        this.course = course;
    }
    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getFileType() { return fileType; }

    public void setFileType(String fileType) { this.fileType = fileType; }

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
