package com.example.edutrack.curriculum.repository;


import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Enrollment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    @Query("SELECT e.courseMentor " +
            "FROM Enrollment e " +
            "WHERE e.isApproved = true AND e.isPaid = true " +
            "GROUP BY e.courseMentor " +
            "ORDER BY COUNT(e.id) DESC")
    List<CourseMentor> findPopularCoursesByEnrollmentCount(Pageable pageable);

    Enrollment findByCourseMentorId(UUID courseMentorId);

    @Query("SELECT e.totalSessions FROM Enrollment e WHERE e.courseMentor.id = :courseMentorId AND e.isApproved = true")
    Integer totalSessionsByCourseMentorId(@Param("courseMentorId") UUID courseMentorId);

    int countByCourseMentor_IdAndIsApprovedTrue(UUID courseMentorId);

    @Query("""
    SELECT e FROM Enrollment e
    WHERE e.mentee.id = :menteeId
      AND e.status = com.example.edutrack.curriculum.model.EnrollmentStatus.Accepted
      AND e.isApproved = true
      AND e.isPaid = true
""")
    List<Enrollment> findAcceptedEnrollmentsByMenteeId(UUID menteeId);
}
