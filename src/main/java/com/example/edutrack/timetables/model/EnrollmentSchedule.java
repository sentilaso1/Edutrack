package com.example.edutrack.timetables.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    public enum RescheduleStatus {
        NONE, REQUESTED, APPROVED, REJECTED;

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

    @Column(name = "report")
    private Boolean report;

    @Column(name = "title_section")
    private String titleSection;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "reschedule_status", nullable = false)
    private RescheduleStatus rescheduleStatus = RescheduleStatus.NONE;

    @Column(name = "reschedule_reason", columnDefinition = "TEXT")
    private String rescheduleReason;

    @Column(name = "reschedule_request_date")
    private LocalDate rescheduleRequestDate;


    @Enumerated(EnumType.STRING)
    @Column(name = "requested_new_slot")
    private Slot requestedNewSlot;

    @Column(name = "requested_new_date")
    private LocalDate requestedNewDate;

    @Column(name = "available")
    private boolean available = true;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public EnrollmentSchedule() {}

    public EnrollmentSchedule(Enrollment enrollment, Slot slot, LocalDate date) {
        this.enrollment = enrollment;
        this.slot = slot;
        this.date = date;
    }

    // --- Getters and Setters ---

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

    public Boolean getReport() {
        return report;
    }

    public void setReport(Boolean report) {
        this.report = report;
    }

    public String getTitleSection() {
        return titleSection;
    }

    public void setTitleSection(String titleSection) {
        this.titleSection = titleSection;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RescheduleStatus getRescheduleStatus() {
        return rescheduleStatus;
    }

    public void setRescheduleStatus(RescheduleStatus rescheduleStatus) {
        this.rescheduleStatus = rescheduleStatus;
    }

    public String getRescheduleReason() {
        return rescheduleReason;
    }

    public void setRescheduleReason(String rescheduleReason) {
        this.rescheduleReason = rescheduleReason;
    }

    public LocalDate getRescheduleRequestDate() {
        return rescheduleRequestDate;
    }

    public void setRescheduleRequestDate(LocalDate rescheduleRequestDate) {
        this.rescheduleRequestDate = rescheduleRequestDate;
    }

    public Slot getRequestedNewSlot() {
        return requestedNewSlot;
    }

    public void setRequestedNewSlot(Slot requestedNewSlot) {
        this.requestedNewSlot = requestedNewSlot;
    }

    public LocalDate getRequestedNewDate() {
        return requestedNewDate;
    }

    public void setRequestedNewDate(LocalDate requestedNewDate) {
        this.requestedNewDate = requestedNewDate;
    }

    public boolean canSubmitFeedback() {
        long days = ChronoUnit.DAYS.between(this.date, LocalDate.now());
        return this.attendance != Attendance.NOT_YET
                && this.report == null
                && days >= 0 && days <= 7;
    }

    public boolean isFeedbackExpired() {
        long days = ChronoUnit.DAYS.between(this.date, LocalDate.now());
        return this.attendance != Attendance.NOT_YET
                && this.report == null
                && days > 7;
    }

    public boolean canRequestReschedule() {
        return this.attendance == Attendance.NOT_YET
                && this.rescheduleStatus == RescheduleStatus.NONE
                && this.date.isAfter(LocalDate.now().plusDays(1));
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
                ", report=" + report +
                ", rescheduleStatus=" + rescheduleStatus +
                ", requestedNewSlot=" + requestedNewSlot +
                ", requestedNewDate=" + requestedNewDate +
                '}';
    }
}
