package com.example.edutrack.timetables.dto;

import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Slot;
import jakarta.annotation.Nonnull;

import java.io.Serializable;
import java.time.LocalDate;

public class RequestedSchedule implements Serializable {
    @Nonnull
    Slot slot;

    @Nonnull
    Day day;

    @Nonnull
    LocalDate requestedDate;

    public RequestedSchedule(@Nonnull Slot slot, @Nonnull Day day, @Nonnull LocalDate requestedDate) {
        this.slot = slot;
        this.day = day;
        this.requestedDate = requestedDate;
    }

    public Slot getSlot() {
        return slot;
    }
    public Day getDay() {
        return day;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

}
