package com.example.edutrack.timetables.dto;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.curriculum.model.CourseMentor;

public record EnrollmentAttendanceDTO(Long id, Mentee mentee, CourseMentor courseMentor) {
    @Override
    public Long id() {
        return id;
    }

    @Override
    public Mentee mentee() {
        return mentee;
    }

    @Override
    public CourseMentor courseMentor() {
        return courseMentor;
    }
}
