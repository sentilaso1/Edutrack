package com.example.edutrack.timetables.model;

import java.time.LocalTime;

public enum Slot {
    SLOT_1(LocalTime.of(7, 30), LocalTime.of(8, 15)),
    SLOT_2(LocalTime.of(8, 15), LocalTime.of(9, 0)),
    SLOT_3(LocalTime.of(9, 0), LocalTime.of(9, 45)),
    SLOT_4(LocalTime.of(9, 45), LocalTime.of(10, 30)),
    SLOT_5(LocalTime.of(10, 30), LocalTime.of(11, 15)),
    SLOT_6(LocalTime.of(11, 15), LocalTime.of(12, 0)),

    // Lunch break: 12:00 – 1:00 (not represented as a slot)

    SLOT_7(LocalTime.of(13, 0), LocalTime.of(13, 45)),
    SLOT_8(LocalTime.of(13, 45), LocalTime.of(14, 30)),
    SLOT_9(LocalTime.of(14, 30), LocalTime.of(15, 15)),
    SLOT_10(LocalTime.of(15, 15), LocalTime.of(16, 0)),
    SLOT_11(LocalTime.of(16, 0), LocalTime.of(16, 45)),
    SLOT_12(LocalTime.of(16, 45), LocalTime.of(17, 30)),
    SLOT_13(LocalTime.of(17, 30), LocalTime.of(18, 15)),

    // Dinner break: 6:00 – 7:00 (not represented as a slot)

    SLOT_14(LocalTime.of(19, 0), LocalTime.of(19, 45)),
    SLOT_15(LocalTime.of(19, 45), LocalTime.of(20, 30)),
    SLOT_16(LocalTime.of(20, 30), LocalTime.of(21, 15));

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
