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
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    @Query("""
            SELECT e.courseMentor
            FROM Enrollment e
            WHERE e.status = Enrollment.EnrollmentStatus.APPROVED
            GROUP BY e.courseMentor
            ORDER BY COUNT(e.id) DESC
           """)
    List<CourseMentor> findPopularCoursesByEnrollmentCount(Pageable pageable);


    int countByCourseMentor_IdAndIsApprovedTrue(UUID courseMentorId);

    @Query("""
            SELECT e FROM Enrollment e
            WHERE e.mentee.id = :menteeId
              AND e.status = Enrollment.EnrollmentStatus.APPROVED
           """)
    List<Enrollment> findAcceptedEnrollmentsByMenteeId(@Param("menteeId") UUID menteeId);

    @Query("""
            SELECT DISTINCT e.courseMentor
            FROM Enrollment e
            JOIN Schedule s ON s.mentee = e.mentee AND s.course = e.courseMentor.course
            WHERE e.mentee.id = :menteeId
              AND e.status = Enrollment.EnrollmentStatus.APPROVED
              AND s.date >= CURRENT_DATE
           """)
    List<CourseMentor> findInProgressCourses(@Param("menteeId") UUID menteeId);
}
