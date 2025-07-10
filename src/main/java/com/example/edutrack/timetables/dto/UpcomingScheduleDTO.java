package com.example.edutrack.timetables.dto;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.timetables.model.EnrollmentSchedule;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record UpcomingScheduleDTO(EnrollmentSchedule schedule, Course course, Mentee mentee) implements Serializable {
    public EnrollmentSchedule getSchedule() {
        return schedule;
    }

    public Course getCourse() {
        return course;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public String getFormattedDate() {
        if (schedule == null || schedule.getDate() == null) {
            return "";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy");
        return (schedule.getDate().format(formatter)) + " - " + schedule.getSlot().getFormattedTime();
    }

    @Override
    public String toString() {
        return "UpcomingScheduleDTO{" +
                "schedule=" + schedule +
                ", course=" + course +
                ", mentee=" + mentee +
                '}';
    }
}
