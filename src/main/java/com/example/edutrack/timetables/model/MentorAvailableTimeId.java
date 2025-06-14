package com.example.edutrack.timetables.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;
import java.util.Date;

@Embeddable
public class MentorAvailableTimeId implements Serializable {
    @Column(name = "mentor_id", nullable = false)
    private UUID mentorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot", nullable = false)
    private Slot slot;

    @Enumerated(EnumType.STRING)
    @Column(name = "day", nullable = false)
    private Day day;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    public MentorAvailableTimeId() {
    }

    public MentorAvailableTimeId(UUID mentorId, Slot slot, Day day, Date startDate, Date endDate) {
        this.mentorId = mentorId;
        this.slot = slot;
        this.day = day;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getMentorId() {
        return mentorId;
    }

    public void setMentorId(UUID mentorId) {
        this.mentorId = mentorId;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "MentorAvailableTimeId{" +
                "mentorId=" + mentorId +
                ", slot=" + slot +
                ", day=" + day +
                '}';
    }
}
