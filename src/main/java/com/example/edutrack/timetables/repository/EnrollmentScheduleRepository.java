package com.example.edutrack.timetables.repository;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface EnrollmentScheduleRepository extends JpaRepository<EnrollmentSchedule, Integer> {
    @Query("SELECT 1 FROM EnrollmentSchedule es " +
           "JOIN Enrollment e ON e = es.enrollment " +
           "WHERE e.courseMentor.mentor = :mentor " +
           "AND es.date = :date AND es.slot = :slot AND WEEKDAY(es.date) = :day")
    public boolean isAvailableSlot(Slot slot, Day day, Date date, User mentor);
}
