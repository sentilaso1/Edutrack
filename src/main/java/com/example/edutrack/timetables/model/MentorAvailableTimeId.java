package com.example.edutrack.timetables.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

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

    public MentorAvailableTimeId() {
    }

    public MentorAvailableTimeId(UUID mentorId, Slot slot, Day day) {
        this.mentorId = mentorId;
        this.slot = slot;
        this.day = day;
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

    @Override
    public String toString() {
        return "MentorAvailableTimeId{" +
                "mentorId=" + mentorId +
                ", slot=" + slot +
                ", day=" + day +
                '}';
    }
}
