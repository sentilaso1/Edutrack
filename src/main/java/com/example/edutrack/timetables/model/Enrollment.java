package com.example.edutrack.timetables.model;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.CourseMentor;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "enrollments")
public class Enrollment {
    public enum EnrollmentStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnrollmentStatus status = EnrollmentStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "mentee_id", nullable = false)
    private Mentee mentee;

    @ManyToOne
    @JoinColumn(name = "course_mentor_id", nullable = false)
    private CourseMentor courseMentor;

    @Column(name = "total_slots")
    private Integer totalSlots;

    @Column(name = "start_time")
    private String startTime; //startDate,startSlot => many slots in a day

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    @Column(name="schedule_summary")
    private String scheduleSummary; // mon-slot1, thu-slot2

    public Enrollment() {

    }

    public Enrollment(Mentee mentee, CourseMentor courseMentor, Integer totalSlots, Slot slot, Day day) {
        this.mentee = mentee;
        this.courseMentor = courseMentor;
        this.totalSlots = totalSlots;
        this.scheduleSummary = day+"-"+slot;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }

    public CourseMentor getCourseMentor() {
        return courseMentor;
    }

    public void setCourseMentor(CourseMentor courseMentor) {
        this.courseMentor = courseMentor;
    }

    public Integer getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(Integer totalSlots) {
        this.totalSlots = totalSlots;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getScheduleSummary() {
        return scheduleSummary;
    }

    public void setScheduleSummary(String scheduleSummary) {
        this.scheduleSummary = scheduleSummary;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
               "id=" + id +
               ", status=" + status +
               ", mentee=" + mentee +
               ", courseMentor=" + courseMentor +
               ", totalSlots=" + totalSlots +
               ", startTime='" + startTime + '\'' +
               ", createdDate=" + createdDate +
               ", scheduleSummary='" + scheduleSummary + '\'' +
               '}';
    }
}
