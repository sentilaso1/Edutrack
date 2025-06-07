package com.example.edutrack.timetables.model;

import java.time.LocalTime;

public enum Slot {
    SLOT_1(LocalTime.of(7, 30), LocalTime.of(9, 0)),
    SLOT_2(LocalTime.of(9, 30), LocalTime.of(11, 0)),
    SLOT_3(LocalTime.of(14, 30), LocalTime.of(16, 0)),
    SLOT_4(LocalTime.of(16, 30), LocalTime.of(18, 0)),
    SLOT_5(LocalTime.of(19, 30), LocalTime.of(21, 0));

    private final LocalTime startTime;
    private final LocalTime endTime;

    Slot(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
