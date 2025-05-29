package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    @Query("SELECT c FROM Course c " +
            "LEFT JOIN c.mentor m " +
            "WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:mentorSearch IS NULL OR LOWER(m.fullName) LIKE LOWER(CONCAT('%', :mentorSearch, '%'))) " +
            "AND (:open IS NULL OR c.isOpen = :open) " +
            "AND (:fromDate IS NULL OR c.createdDate >= :fromDate) " +
            "AND (:toDate IS NULL OR c.createdDate <= :toDate) " +
            "ORDER BY c.name ASC")
    List<Course> findFilteredCoursesOrderByName(
            @Param("search") String search,
            @Param("mentorSearch") String mentorSearch,
            @Param("open") Boolean open,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    @Query("SELECT c FROM Course c " +
            "LEFT JOIN c.mentor m " +
            "WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:mentorSearch IS NULL OR LOWER(m.fullName) LIKE LOWER(CONCAT('%', :mentorSearch, '%'))) " +
            "AND (:open IS NULL OR c.isOpen = :open) " +
            "AND (:fromDate IS NULL OR c.createdDate >= :fromDate) " +
            "AND (:toDate IS NULL OR c.createdDate <= :toDate) " +
            "ORDER BY c.createdDate DESC")
    List<Course> findFilteredCoursesOrderByCreatedDate(
            @Param("search") String search,
            @Param("mentorSearch") String mentorSearch,
            @Param("open") Boolean open,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    @Query("SELECT c FROM Course c " +
            "LEFT JOIN c.mentor m " +
            "WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:mentorSearch IS NULL OR LOWER(m.fullName) LIKE LOWER(CONCAT('%', :mentorSearch, '%'))) " +
            "AND (:open IS NULL OR c.isOpen = :open) " +
            "AND (:fromDate IS NULL OR c.createdDate >= :fromDate) " +
            "AND (:toDate IS NULL OR c.createdDate <= :toDate) " +
            "ORDER BY m.fullName ASC NULLS LAST")
    List<Course> findFilteredCoursesOrderByMentorName(
            @Param("search") String search,
            @Param("mentorSearch") String mentorSearch,
            @Param("open") Boolean open,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    @Query("SELECT c FROM Course c " +
            "LEFT JOIN c.mentor m " +
            "WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:mentorSearch IS NULL OR LOWER(m.fullName) LIKE LOWER(CONCAT('%', :mentorSearch, '%'))) " +
            "AND (:open IS NULL OR c.isOpen = :open) " +
            "AND (:fromDate IS NULL OR c.createdDate >= :fromDate) " +
            "AND (:toDate IS NULL OR c.createdDate <= :toDate) " +
            "ORDER BY c.createdDate DESC")
    List<Course> findFilteredCoursesDefault(
            @Param("search") String search,
            @Param("mentorSearch") String mentorSearch,
            @Param("open") Boolean open,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate);

    @Query("SELECT c FROM Course c ORDER BY c.createdDate DESC")
    List<Course> findAllOrderByCreatedDate();
}