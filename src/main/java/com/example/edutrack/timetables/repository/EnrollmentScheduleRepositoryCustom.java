package com.example.edutrack.timetables.repository;

import com.example.edutrack.timetables.dto.EnrollmentAttendanceDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EnrollmentScheduleRepositoryCustom {
    Page<EnrollmentAttendanceDTO> findScheduleToBeConfirmedFiltered(Pageable pageable, String menteeId, String mentorId);
}
