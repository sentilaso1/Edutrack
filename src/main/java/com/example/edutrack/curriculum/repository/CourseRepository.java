package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {

    @Query("""
                SELECT DISTINCT c FROM Course c
                LEFT JOIN CourseMentor cm ON cm.course = c
                LEFT JOIN Mentor m ON m = cm.mentor
                WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:mentorSearch IS NULL OR LOWER(m.fullName) LIKE LOWER(CONCAT('%', :mentorSearch, '%')))
                AND (:open IS NULL OR c.isOpen = :open)
                AND (:fromDate IS NULL OR c.createdDate >= :fromDate)
                AND (:toDate IS NULL OR c.createdDate <= :toDate)
                ORDER BY c.name ASC
            """)
    Page<Course> findFilteredCoursesOrderByName(
            @Param("search") String search,
            @Param("mentorSearch") String mentorSearch,
            @Param("open") Boolean open,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            Pageable pageable);

    @Query("""
                SELECT DISTINCT c FROM Course c
                LEFT JOIN CourseMentor cm ON cm.course = c
                LEFT JOIN Mentor m ON m = cm.mentor
                WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:mentorSearch IS NULL OR LOWER(m.fullName) LIKE LOWER(CONCAT('%', :mentorSearch, '%')))
                AND (:open IS NULL OR c.isOpen = :open)
                AND (:fromDate IS NULL OR c.createdDate >= :fromDate)
                AND (:toDate IS NULL OR c.createdDate <= :toDate)
                ORDER BY c.createdDate DESC
            """)
    Page<Course> findFilteredCoursesOrderByCreatedDate(
            @Param("search") String search,
            @Param("mentorSearch") String mentorSearch,
            @Param("open") Boolean open,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            Pageable pageable);

    @Query("""
                SELECT DISTINCT c FROM Course c
                LEFT JOIN CourseMentor cm ON cm.course = c
                LEFT JOIN Mentor m ON m = cm.mentor
                WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:mentorSearch IS NULL OR LOWER(m.fullName) LIKE LOWER(CONCAT('%', :mentorSearch, '%')))
                AND (:open IS NULL OR c.isOpen = :open)
                AND (:fromDate IS NULL OR c.createdDate >= :fromDate)
                AND (:toDate IS NULL OR c.createdDate <= :toDate)
                ORDER BY m.fullName ASC NULLS LAST
            """)
    Page<Course> findFilteredCoursesOrderByMentorName(
            @Param("search") String search,
            @Param("mentorSearch") String mentorSearch,
            @Param("open") Boolean open,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            Pageable pageable);

    @Query("""
                SELECT DISTINCT c FROM Course c
                LEFT JOIN CourseMentor cm ON cm.course = c
                LEFT JOIN Mentor m ON m = cm.mentor
                WHERE (:search IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:mentorSearch IS NULL OR LOWER(m.fullName) LIKE LOWER(CONCAT('%', :mentorSearch, '%')))
                AND (:open IS NULL OR c.isOpen = :open)
                AND (:fromDate IS NULL OR c.createdDate >= :fromDate)
                AND (:toDate IS NULL OR c.createdDate <= :toDate)
                ORDER BY c.createdDate DESC
            """)
    Page<Course> findFilteredCoursesDefault(
            @Param("search") String search,
            @Param("mentorSearch") String mentorSearch,
            @Param("open") Boolean open,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            Pageable pageable);

    @Query("SELECT c FROM Course c ORDER BY c.createdDate DESC")
    Page<Course> findAllOrderByCreatedDate(Pageable pageable);

    @Query("""
                SELECT c FROM Course c
                WHERE
                    (:skillIds IS NULL OR c.id IN :skillIds)
                AND
                    (:subjectIds IS NULL OR EXISTS (
                        SELECT 1 FROM CourseTag ct
                        WHERE ct.course = c
                        AND ct.tag.id IN :subjectIds
                    ))
            """)
    Page<Course> findFilteredCourses(
            @Param("skillIds") List<UUID> skillIds,
            @Param("subjectIds") List<Integer> subjectIds,
            Pageable pageable
    );

    Long countByIsOpen(Boolean isOpen);
}
