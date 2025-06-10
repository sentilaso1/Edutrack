package com.example.edutrack.timetables.repository;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EnrollmentScheduleRepository extends JpaRepository<EnrollmentSchedule, Integer> {
    @Query("SELECT COUNT(e) > 0 FROM Enrollment e " +
           "JOIN EnrollmentSchedule es ON es.enrollment = e " +
           "WHERE e.courseMentor = :courseMentor " +
           "AND es.slot = :slot " +
           "AND FUNCTION('WEEKDAY', es.date) = :day " +
           "AND es.date = :date")
    boolean isTakenSlot(@Param("courseMentor") CourseMentor courseMentor,
                        @Param("slot") Slot slot,
                        @Param("day") Day day,
                        @Param("date") LocalDate date);


    @Query("SELECT COUNT(e) > 0 FROM Enrollment e " +
           "JOIN EnrollmentSchedule es ON es.enrollment = e " +
           "WHERE e.mentee = :user " +
           "AND es.slot = :slot " +
           "AND FUNCTION('WEEKDAY', es.date) = :day " +
           "AND es.date = :date")
    boolean isLearningSlot(@Param("user") Mentee user,
                           @Param("slot") Slot slot,
                           @Param("day") Day day,
                           @Param("date") LocalDate date);

}
