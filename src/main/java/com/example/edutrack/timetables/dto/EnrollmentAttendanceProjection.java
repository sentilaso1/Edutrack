package com.example.edutrack.timetables.dto;

import java.util.UUID;

public interface EnrollmentAttendanceProjection {
    Long getId();
    UUID getMenteeId();
    UUID getCourseMentorId();
}
