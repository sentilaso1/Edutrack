package com.example.edutrack.curriculum.model;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.util.UUID;

@Entity
@Table(name = "mentor_available_time")
@IdClass(MentorAvailableTimeId.class)
public class MentorAvailableTime {
    @Id
    private UUID mentorId;

    @Id
    private Integer slotId;

    @Id
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    @ManyToOne
    @JoinColumn(name = "slotId", insertable = false, updatable = false)
    private Slot slot;

    public MentorAvailableTime() {}


    public UUID getMentorId() {
        return mentorId;
    }
    public void setMentorId(UUID mentorId) {
        this.mentorId = mentorId;
    }

    public Slot getSlot() {
        return slot;
    }


    public DayOfWeek getDay() {
        return day;
    }

    @Override
    public String toString() {
        return "MentorAvailableTime {mentorId=" + mentorId + ", slotId=" + slotId + ", day=" + day + "}";
    }
}
