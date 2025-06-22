package com.example.edutrack.timetables.dto;

import com.example.edutrack.accounts.model.Mentor;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;

public class MentorAvailableTimeDTO implements Serializable {
    @NotNull
    LocalDate startDate;
    @NotNull
    LocalDate endDate;

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    @NotNull
    Mentor mentor;

    public MentorAvailableTimeDTO() {
    }

    public MentorAvailableTimeDTO(LocalDate startDate, LocalDate endDate, Mentor mentor) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.mentor = mentor;
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
