package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.ApprovalStatus;
import com.example.edutrack.curriculum.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    @Query("SELECT c FROM Course c " +
            "WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:mentorId IS NULL OR c.mentor.id = :mentorId) " +
            "AND (:approvalStatus IS NULL OR c.approvalStatus = :approvalStatus) " +
            "AND (:open IS NULL OR c.isOpen = :open) " +
            "AND (:fromDate IS NULL OR c.createdDate >= :fromDate) " +
            "AND (:toDate IS NULL OR c.createdDate <= :toDate) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'name' THEN c.name END ASC, " +
            "CASE WHEN :sortBy = 'createdDate' THEN c.createdDate END DESC")
    List<Course> findFilteredCourses(
            @Param("search") String search,
            @Param("mentorId") UUID mentorId,
            @Param("approvalStatus") ApprovalStatus approvalStatus,
            @Param("open") Boolean open,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("sortBy") String sortBy);


}
