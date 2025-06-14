package com.example.edutrack.timetables.dto;

import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Slot;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

public class MentorAvailableSlotDTO implements Serializable {
    @NotNull
    private Slot slot;

    @NotNull
    private Day day;

    public MentorAvailableSlotDTO() {

    }

    public MentorAvailableSlotDTO(Slot slot, Day day) {
        this.slot = slot;
        this.day = day;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MentorAvailableSlotDTO that = (MentorAvailableSlotDTO) o;
        return slot == that.slot && day == that.day;
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, day);
    }


}
