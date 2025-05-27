package com.example.edutrack.curriculum.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.UUID;

public class MentorAvailableTimeId implements Serializable {
    private UUID mentorId;
    private Integer slotId;
    private DayOfWeek day;

    public MentorAvailableTimeId() {}

    public MentorAvailableTimeId(UUID mentorId, Integer slotId, DayOfWeek day) {
        this.mentorId = mentorId;
        this.slotId = slotId;
        this.day = day;
    }
    public UUID getMentorId() {
        return mentorId;
    }
    public void setMentorId(UUID mentorId) {
        this.mentorId = mentorId;
    }
    public Integer getSlotId() {
        return slotId;
    }
    public void setSlotId(Integer slotId) {
        this.slotId = slotId;
    }
    public DayOfWeek getDay() {
        return day;
    }
    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public String toString() {
        return "MentorAvailableTimeId{" + "mentorId=" + mentorId + ", slotId=" + slotId + ", day=" + day + "}";
    }
}