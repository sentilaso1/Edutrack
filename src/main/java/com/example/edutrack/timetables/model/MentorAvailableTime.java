package com.example.edutrack.timetables.model;

import com.example.edutrack.accounts.model.Mentor;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "mentor_available_time")
public class MentorAvailableTime {
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
        DRAFT,
        CANCELLED;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    @EmbeddedId
    private MentorAvailableTimeId id;

    @ManyToOne
    @MapsId("mentorId")
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.DRAFT;

    @Column(name = "reason")
    private String reason;

    @Column(name = "created_date")
    @CreatedDate
    private Date createdDate = new Date();

    public MentorAvailableTime() {
    }

    public MentorAvailableTime(Mentor mentor, Slot slot, Day day, LocalDate startDate, LocalDate endDate) {
        this.id = new MentorAvailableTimeId(mentor.getId(), slot, day, startDate, endDate);
        this.mentor = mentor;
    }

    public MentorAvailableTimeId getId() {
        return id;
    }

    public void setId(MentorAvailableTimeId id) {
        this.id = id;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "MentorAvailableTime{" +
                "id=" + id +
                ", mentor=" + mentor +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
