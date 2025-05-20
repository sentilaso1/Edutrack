package com.example.edutrack.curriculum.model;

import com.example.edutrack.accounts.model.Mentor;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "course_mentors")
public class CourseMentor {

    @EmbeddedId
    private CourseMentorId id;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @MapsId("mentorId")
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    public CourseMentor() {
    }

    public CourseMentor(CourseMentorId id, Date createdDate) {
        this.id = id;
        this.createdDate = createdDate;
    }

    public CourseMentorId getId() {
        return id;
    }

    public void setId(CourseMentorId id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "CourseMentor{" +
                "id=" + id +
                ", createdDate=" + createdDate +
                '}';
    }
}
