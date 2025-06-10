package com.example.edutrack.timetables.repository;

import com.example.edutrack.timetables.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT COUNT(s) FROM Schedule s WHERE s.mentee.id = :menteeId AND s.attendance = com.example.edutrack.timetables.model.Schedule.ScheduleAttendance.PRESENT")
    int countCompletedAttendanceByMenteeId(@Param("menteeId") UUID menteeId);

}
