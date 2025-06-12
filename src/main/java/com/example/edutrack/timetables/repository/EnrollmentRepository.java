package com.example.edutrack.timetables.repository;

import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.model.Enrollment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
              AND s.date >= CURRENT_DATE
           """)
    List<CourseMentor> findInProgressCourses(@Param("menteeId") UUID menteeId, @Param("enrollmentStatus")Enrollment.EnrollmentStatus status);

    List<Enrollment> findByStatus(Enrollment.EnrollmentStatus status);


}
