package com.example.edutrack.timetables.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "enrollment_schedule")
public class EnrollmentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot", nullable = false)
    private Slot slot;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "is_test")
    private Boolean isTest = Boolean.FALSE;

    @Column(name = "score")
    private Integer score;

    public EnrollmentSchedule() {
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Enrollment getEnrollment() {
        return enrollment;
    }
    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }
    public Slot getSlot() {
        return slot;
    }
    public void setSlot(Slot slot) {
        this.slot = slot;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public Boolean getIsTest() {
        return isTest;
    }
    public void setIsTest(Boolean isTest) {
        this.isTest = isTest;
    }
    public Integer getScore() {
        return score;
    }
    public void setScore(Integer score) {
        this.score = score;
    }



}
