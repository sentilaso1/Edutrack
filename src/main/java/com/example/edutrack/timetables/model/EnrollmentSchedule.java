package com.example.edutrack.timetables.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a single, specific learning session for a mentee's enrollment in a course.
 * Each instance corresponds to one scheduled class on a particular date and slot.
 */
@Entity
@Table(name = "enrollment_schedule")
public class EnrollmentSchedule {

    /**
     * Defines the attendance status of a mentee for a session.
     */
    public enum Attendance {
        NOT_YET,
        PRESENT,
        ABSENT,
        CANCELLED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public enum RescheduleStatus {
        /** No reschedule request has been made. This is the default state. */
        NONE,
        /** The mentee has submitted a request to reschedule, and it is pending mentor approval. */
        REQUESTED,
        /** The mentor has approved the reschedule request. The schedule's date/slot will be updated. */
        APPROVED,
        /** The mentor has rejected the reschedule request, or the system automatically rejected it. */
        REJECTED;

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


    /** The current status of any reschedule request for this session. */
    @Enumerated(EnumType.STRING)
    @Column(name = "reschedule_status", nullable = false)
    private RescheduleStatus rescheduleStatus = RescheduleStatus.NONE;

    /**
     * Stores the reason for a reschedule request.
     * This field serves two purposes:
     * 1. Stores the reason provided by the mentee.
     * 2. Stores system-generated codes (e.g., "AUTO_REJECTED_EXPIRED_ORIGINAL_DATE") for automated actions.
     */
    @Column(name = "reschedule_reason", columnDefinition = "TEXT")
    private String rescheduleReason;

    /** The date when the mentee initiated the reschedule request. */
    @Column(name = "reschedule_request_date")
    private LocalDate rescheduleRequestDate;

    /** The new time slot the mentee has requested. */
    @Enumerated(EnumType.STRING)
    @Column(name = "requested_new_slot")
    private Slot requestedNewSlot;

    /** The new date the mentee has requested. */
    @Column(name = "requested_new_date")
    private LocalDate requestedNewDate;

    /**
     * Timestamp of the last update to the reschedule status.
     * This is crucial for the "Recent Activities" banner to know if an event is recent (e.g., within 7 days).
     */
    @Column(name = "reschedule_status_update_date")
    private LocalDateTime rescheduleStatusUpdateDate;


    // --- General State Fields ---

    /**
     * A master switch to enable/disable the session.
     * If false, the session is considered inactive and cannot be interacted with,
     * regardless of other statuses. Used by the system to void a session (e.g., after an expired request).
     */
    @Column(name = "available")
    private boolean available = true;

    public EnrollmentSchedule() {}

    public EnrollmentSchedule(Enrollment enrollment, Slot slot, LocalDate date) {
        this.enrollment = enrollment;
        this.slot = slot;
        this.date = date;
    }


    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }
    public Slot getSlot() { return slot; }
    public void setSlot(Slot slot) { this.slot = slot; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Boolean getTest() { return isTest; }
    public void setTest(Boolean test) { isTest = test; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Attendance getAttendance() { return attendance; }
    public void setAttendance(Attendance attendance) { this.attendance = attendance; }
    public Boolean getReport() { return report; }
    public void setReport(Boolean report) { this.report = report; }
    public String getTitleSection() { return titleSection; }
    public void setTitleSection(String titleSection) { this.titleSection = titleSection; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public RescheduleStatus getRescheduleStatus() { return rescheduleStatus; }
    public void setRescheduleStatus(RescheduleStatus rescheduleStatus) { this.rescheduleStatus = rescheduleStatus; }
    public String getRescheduleReason() { return rescheduleReason; }
    public void setRescheduleReason(String rescheduleReason) { this.rescheduleReason = rescheduleReason; }
    public LocalDate getRescheduleRequestDate() { return rescheduleRequestDate; }
    public void setRescheduleRequestDate(LocalDate rescheduleRequestDate) { this.rescheduleRequestDate = rescheduleRequestDate; }
    public LocalDateTime getRescheduleStatusUpdateDate() { return rescheduleStatusUpdateDate; }
    public void setRescheduleStatusUpdateDate(LocalDateTime rescheduleStatusUpdateDate) { this.rescheduleStatusUpdateDate = rescheduleStatusUpdateDate; }
    public Slot getRequestedNewSlot() { return requestedNewSlot; }
    public void setRequestedNewSlot(Slot requestedNewSlot) { this.requestedNewSlot = requestedNewSlot; }
    public LocalDate getRequestedNewDate() { return requestedNewDate; }
    public void setRequestedNewDate(LocalDate requestedNewDate) { this.requestedNewDate = requestedNewDate; }


    /**
     * Checks if the mentee is allowed to submit feedback for this session.
     * Conditions: The session must have a final attendance status, no report submitted yet,
     * and it has been 0 to 7 days since the session date.
     * @return true if feedback can be submitted, false otherwise.
     */
    public boolean canSubmitFeedback() {
        LocalDate today = LocalDate.now();
        LocalDate sessionDate = this.date;
        LocalDate deadline = sessionDate.plusDays(7);
        return this.attendance != Attendance.NOT_YET
                && this.report == null
                && !today.isBefore(sessionDate)
                && !today.isAfter(deadline);
    }

    /**
     * Checks if the time window for submitting feedback has expired.
     * Conditions: The session has a final attendance status, no report submitted yet,
     * and it has been more than 7 days since the session date.
     * @return true if the feedback window is expired, false otherwise.
     */
    public boolean isFeedbackExpired() {
        long days = ChronoUnit.DAYS.between(this.date, LocalDate.now());
        return this.attendance != Attendance.NOT_YET
                && this.report == null
                && days > 7;
    }

    /**
     * Checks if the mentee is allowed to request a reschedule for this session.
     * Conditions: The session has not happened yet, there are no pending reschedule requests,
     * and the session date is in the future.
     * @return true if a reschedule can be requested, false otherwise.
     */
    public boolean canRequestReschedule() {
        return this.attendance == Attendance.NOT_YET
                && this.rescheduleStatus == RescheduleStatus.NONE
                && this.date.isAfter(LocalDate.now());
    }

    @Override
    public String toString() {
        return "EnrollmentSchedule{" +
                "id=" + id +
                ", enrollment=" + enrollment.getId() +
                ", slot=" + slot +
                ", date=" + date +
                ", attendance=" + attendance +
                ", rescheduleStatus=" + rescheduleStatus +
                '}';
    }
}