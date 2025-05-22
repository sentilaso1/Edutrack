package com.example.edutrack.curriculum.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public class MentorAvailableTimeDTO {
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;

    public MentorAvailableTimeDTO(DayOfWeek day, LocalTime startTime, LocalTime endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public DayOfWeek getDay() {
        return day;
    }
    public void setDay(DayOfWeek day) {
        this.day = day;
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    public LocalTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
