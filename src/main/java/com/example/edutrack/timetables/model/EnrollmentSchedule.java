package com.example.edutrack.timetables.model;

import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
@Table(name = "enrollment_schedule")
public class EnrollmentSchedule {
    public enum Attendance {
        NOT_YET, PRESENT, ABSENT, CANCELLED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

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
    private LocalDate date;

    @Column(name = "is_test")
    private Boolean isTest = Boolean.FALSE;

    @Column(name = "score")
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance")
    private Attendance attendance = Attendance.NOT_YET;

    public EnrollmentSchedule() {
    }

    public EnrollmentSchedule(Enrollment enrollment, Slot slot, LocalDate date) {
        this.enrollment = enrollment;
        this.slot = slot;
        this.date = date;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getTest() {
        return isTest;
    }

    public void setTest(Boolean test) {
        isTest = test;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    @Override
    public String toString() {
        return "EnrollmentSchedule{" +
               "id=" + id +
               ", enrollment=" + enrollment +
               ", slot=" + slot +
               ", date=" + date +
               ", isTest=" + isTest +
               ", score=" + score +
               ", attendance=" + attendance +
               '}';
    }
}
