package com.example.edutrack.timetables.repository;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EnrollmentScheduleRepository extends JpaRepository<EnrollmentSchedule, Integer> {
    @Query("SELECT COUNT(e) > 0 FROM Enrollment e " +
            "JOIN EnrollmentSchedule es ON es.enrollment = e " +
            "WHERE e.courseMentor.mentor = :mentor " +
            "AND es.slot = :slot " +
            "AND FUNCTION('WEEKDAY', es.date) = :day " +
            "AND es.date = :date")
    boolean isTakenSlot(@Param("mentor") Mentor mentor,
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

    // Author: Nguyen Thanh Vinh
    @Query("SELECT s FROM EnrollmentSchedule s WHERE s.enrollment.mentee.id = :menteeId")
    List<EnrollmentSchedule> findAllByMenteeId(@Param("menteeId") UUID menteeId);

    @Query("""
                SELECT s FROM EnrollmentSchedule s
                WHERE s.date BETWEEN :startDate AND :endDate
                AND s.enrollment.courseMentor.mentor = :mentor
            """)
    List<EnrollmentSchedule> findByMentorAndDateBetween(
            @Param("mentor") Mentor mentor,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Author: Nguyen Thanh Vinh
    @Query("""
                SELECT COUNT(s) FROM EnrollmentSchedule s
                WHERE s.enrollment.mentee.id = :menteeId AND s.attendance = :status
            """)
    int countAttendedSlotsByMenteeId(@Param("menteeId") UUID menteeId, @Param("status") EnrollmentSchedule.Attendance status);

    // Author: Nguyen Thanh Vinh
    @Query("""
                SELECT COUNT(s) FROM EnrollmentSchedule s
                WHERE s.enrollment.mentee.id = :menteeId AND s.attendance != :status
            """)
    int countUnfinishedSlotsByMentee(@Param("menteeId") UUID menteeId, @Param("status") EnrollmentSchedule.Attendance status);

    // Author: Nguyen Thanh Vinh
    @Query("""
                SELECT COUNT(e)
                FROM Enrollment e
                WHERE e.mentee.id = :menteeId
                AND e.status = 'ACCEPTED'
                AND NOT EXISTS (
                    SELECT es
                    FROM EnrollmentSchedule es
                    WHERE es.enrollment = e
                    AND es.attendance <> 'PRESENT'
                )
            """)
    int countCompletedCourseByMentee(@Param("menteeId") UUID menteeId);

    // Author: Nguyen Thanh Vinh
    int countByEnrollment_Id(Long enrollmentId);

    // Author: Nguyen Thanh Vinh
    int countByEnrollment_IdAndAttendance(Long enrollmentId, EnrollmentSchedule.Attendance attendance);

    // Author: Nguyen Thanh Vinh
    @Query("SELECT MAX(es.date) FROM EnrollmentSchedule es WHERE es.enrollment.id = :enrollmentId AND es.attendance = :status")
    LocalDate findLastPresentSessionDate(@Param("enrollmentId") Long enrollmentId, @Param("status") EnrollmentSchedule.Attendance status);

    //Author: Nguyen Thanh Vinh
    @Query("""
    SELECT s FROM EnrollmentSchedule s
    WHERE s.enrollment.mentee.id = :menteeId
      AND MONTH(s.date) = :month
      AND YEAR(s.date) = :year
      AND (:courseId IS NULL OR s.enrollment.courseMentor.course.id = :courseId)
""")
    Page<EnrollmentSchedule> findByMenteeAndMonthWithCourseFilter(
            @Param("menteeId") UUID menteeId,
            @Param("month") int month,
            @Param("year") int year,
            @Param("courseId") UUID courseId,
            Pageable pageable
    );


}
