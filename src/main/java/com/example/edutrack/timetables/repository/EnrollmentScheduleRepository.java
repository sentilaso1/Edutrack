package com.example.edutrack.timetables.repository;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.dto.EnrollmentAttendanceProjection;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Enrollment;
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
    @Query("SELECT s FROM EnrollmentSchedule s WHERE s.enrollment.mentee.id = :menteeId AND s.available = true")
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
                  AND s.available = true
            """)
    Page<EnrollmentSchedule> findByMenteeAndMonthWithCourseFilter(
            @Param("menteeId") UUID menteeId,
            @Param("month") int month,
            @Param("year") int year,
            @Param("courseId") UUID courseId,
            Pageable pageable
    );

    List<EnrollmentSchedule> findEnrollmentScheduleByEnrollment(Enrollment enrollment);

    @Query("""
                SELECT s FROM EnrollmentSchedule s
                WHERE s.enrollment.mentee.id = :menteeId
                AND s.enrollment.courseMentor.id = :courseMentorId
                AND s.available = true
            """)
    List<EnrollmentSchedule> findEnrollmentScheduleByMenteeAndCourseMentor(
            @Param("menteeId") UUID menteeId,
            @Param("courseMentorId") UUID courseMentorId);


    @Query("""
            SELECT COUNT(s) FROM EnrollmentSchedule s 
            WHERE s.enrollment.mentee.id = :menteeId
            AND s.isTest = TRUE 
             """)
    int countTestSlotsByEnrollment(@Param("menteeId") UUID menteeId);

    @Query("""
            SELECT COUNT(s) FROM EnrollmentSchedule s
            WHERE s.enrollment.id = :enrollmentId
            """)
    int countTotalSlot(Long enrollmentId);

    List<EnrollmentSchedule> findByEnrollment_MenteeIdAndRescheduleStatusAndRequestedNewDateBetween(
            UUID menteeId,
            EnrollmentSchedule.RescheduleStatus rescheduleStatus,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("SELECT es FROM EnrollmentSchedule es WHERE es.enrollment.mentee.id = :menteeId AND es.enrollment.courseMentor.id = :courseMentorId AND es.rescheduleStatus = 'REQUESTED' AND es.requestedNewDate BETWEEN :startDate AND :endDate")
    List<EnrollmentSchedule> findReviewingSlotsByCourse(UUID menteeId, UUID courseMentorId, LocalDate startDate, LocalDate endDate);


    @Query(value = """
                SELECT
                    e.id AS id,
                    BIN_TO_UUID(e.mentee_id) AS menteeId,
                    BIN_TO_UUID(e.course_mentor_id) AS courseMentorId
                FROM enrollments e
                JOIN enrollment_schedule es ON e.id = es.enrollment_id
                WHERE es.date BETWEEN NOW() - INTERVAL 7 DAY AND NOW()
                GROUP BY e.id
                ORDER BY e.id DESC
            """,
            countQuery = """
                        SELECT COUNT(*)
                        FROM enrollments e
                        JOIN enrollment_schedule es ON e.id = es.enrollment_id
                        WHERE es.date BETWEEN NOW() - INTERVAL 7 DAY AND NOW()
                        GROUP BY e.id
                        ORDER BY e.id DESC
                    """,
            nativeQuery = true)
    Page<EnrollmentAttendanceProjection> findAllSchedulesToBeConfirmed(Pageable pageable);

    @Query(value = """
            SELECT *
            FROM enrollment_schedule es
            WHERE es.enrollment_id = :enrollmentId
              AND es.date BETWEEN NOW() - INTERVAL 7 DAY AND NOW()
            """, nativeQuery = true)
    Page<EnrollmentSchedule> findSchedulesByEnrollment(Long enrollmentId, Pageable pageable);
    List<EnrollmentSchedule> findAllByRescheduleStatus(EnrollmentSchedule.RescheduleStatus status);

    Long countByEnrollmentAndRescheduleStatusNot(Enrollment enrollment, EnrollmentSchedule.RescheduleStatus status);
    @Query("SELECT es FROM EnrollmentSchedule es WHERE es.enrollment.courseMentor.mentor.id = :mentorId AND es.rescheduleStatus = 'REQUESTED'")
    List<EnrollmentSchedule> findPendingRequestsForMentor(@Param("mentorId") UUID mentorId);
}
