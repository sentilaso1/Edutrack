package com.example.edutrack.timetables.dto;

import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Slot;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public class EnrollmentRequestDTO implements Serializable {
    @NotNull
    private Integer totalSlot;
    @NotNull
    private List<Slot> slot;
    @NotNull
    private List<Day> day;

    public EnrollmentRequestDTO() {}

    public EnrollmentRequestDTO(Integer totalSlot, List<Slot> slot, List<Day> day) {
        this.totalSlot = totalSlot;
        this.slot = slot;
        this.day = day;
    }

    public Integer getTotalSlot() {
        return totalSlot;
    }

    public void setTotalSlot(Integer totalSlot) {
        this.totalSlot = totalSlot;
    }

    public List<Slot> getSlot() {
        return slot;
    }

    public void setSlot(List<Slot> slot) {
        this.slot = slot;
    }

    public List<Day> getDay() {
        return day;
    }

    public void setDay(List<Day> day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "EnrollmentRequestDTO{" +
                "totalSlot=" + totalSlot +
                ", slot=" + slot +
                ", day=" + day +
                '}';
    }
}
