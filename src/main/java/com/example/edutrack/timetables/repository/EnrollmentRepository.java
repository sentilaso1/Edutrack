package com.example.edutrack.timetables.repository;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.model.Enrollment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("""
            SELECT e.courseMentor
            FROM Enrollment e
            WHERE e.status = :enrollmentStatus
            GROUP BY e.courseMentor
            ORDER BY COUNT(e.id) DESC
           """)
    List<CourseMentor> findPopularCoursesByEnrollmentCount(Pageable pageable, @Param("enrollmentStatus")Enrollment.EnrollmentStatus status);


    int countByCourseMentor_IdAndStatus(UUID courseMentorId, Enrollment.EnrollmentStatus status);


    @Query("""
            SELECT e FROM Enrollment e
            WHERE e.mentee.id = :menteeId
              AND e.status = :enrollmentStatus
           """)
    List<Enrollment> findAcceptedEnrollmentsByMenteeId(@Param("menteeId") UUID menteeId, @Param("enrollmentStatus")Enrollment.EnrollmentStatus status);

    @Query("""
            SELECT DISTINCT e.courseMentor
            FROM Enrollment e
            JOIN EnrollmentSchedule s ON s.enrollment.mentee = e.mentee AND s.enrollment.courseMentor.course = e.courseMentor.course
            WHERE e.mentee.id = :menteeId
              AND e.status = :enrollmentStatus
           """)
    List<CourseMentor> findInProgressCourses(@Param("menteeId") UUID menteeId, @Param("enrollmentStatus")Enrollment.EnrollmentStatus status);

    @Query("SELECT e FROM Enrollment e WHERE e.courseMentor.mentor.id = :mentorId AND e.status = :status")
    List<Enrollment> findByStatus(Enrollment.EnrollmentStatus status, UUID mentorId);

    @Query("SELECT e.courseMentor.course FROM Enrollment e WHERE e.mentee.id = :menteeId AND e.status = :status ")
    List<Course> findByMenteeIdAndEnrollmentStatus(@Param("menteeId") UUID menteeId, @Param("status") Enrollment.EnrollmentStatus status);

    @Query(value = """
    SELECT DISTINCT e.* FROM enrollments e
    JOIN enrollment_schedule es ON e.id = es.enrollment_id
    JOIN course_mentor cm ON e.course_mentor_id = cm.id
    WHERE e.status = 'APPROVED'
      AND cm.mentor_user_id = :mentorId
      AND es.date <= :today
      AND (
          SELECT COUNT(*) FROM enrollment_schedule
          WHERE enrollment_id = e.id
            AND date <= :today
            AND attendance <> 'CANCELLED'
      ) < e.total_slots
""", nativeQuery = true)
    List<Enrollment> findOngoingEnrollments(@Param("today") LocalDate today, @Param("mentorId") UUID mentorId);

    @Query("SELECT e FROM Enrollment e WHERE e.status = 'APPROVED'")
    List<Enrollment> findAllApprovedEnrollments();


    @Query("SELECT DISTINCT e.courseMentor.mentor FROM Enrollment e WHERE e.mentee.id = :menteeId AND e.status = :status")
    List<Mentor> findDistinctMentorsByMenteeId(@Param("menteeId") UUID menteeId, @Param("status") Enrollment.EnrollmentStatus status);

    @Query("""
            SELECT DISTINCT e.courseMentor
            FROM Enrollment e
            JOIN EnrollmentSchedule s ON s.enrollment.mentee = e.mentee AND s.enrollment.courseMentor.course = e.courseMentor.course
            WHERE e.mentee.id = :menteeId
              AND e.status = :enrollmentStatus
           """)
    List<CourseMentor> findCourseMentorByMentee(@Param("menteeId") UUID menteeId, @Param("enrollmentStatus")Enrollment.EnrollmentStatus status);

    List<Enrollment> findAllByMenteeId(UUID menteeId);
}
