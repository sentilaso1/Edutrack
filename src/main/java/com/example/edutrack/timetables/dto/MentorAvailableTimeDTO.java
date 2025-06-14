package com.example.edutrack.timetables.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;

public class MentorAvailableTimeDTO implements Serializable {
    @NotNull
    LocalDate startDate;
    @NotNull
    LocalDate endDate;

    public MentorAvailableTimeDTO() {
    }

    public MentorAvailableTimeDTO(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "";
    }
}
