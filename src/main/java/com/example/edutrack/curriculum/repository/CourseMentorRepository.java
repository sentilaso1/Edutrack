package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseMentorRepository  extends JpaRepository<CourseMentor, CourseMentorId> {

    @Query("SELECT ct.tag FROM CourseMentor cm JOIN CourseTag ct ON ct.course = cm.course")
    List<Tag> findAllTags();

    @Query("SELECT distinct cm.course FROM CourseMentor cm")
    List<Course> findAllCourses();

    @Query("""
        SELECT cm FROM CourseMentor cm
        WHERE 
            (:skillIds IS NULL OR cm.course.id IN :skillIds)
        AND 
            (:subjectIds IS NULL OR EXISTS (
                SELECT 1 FROM CourseTag ct
                WHERE ct.course = cm.course
                AND ct.tag.id IN :subjectIds
            ))
    """)
    Page<CourseMentor> findFilteredCourseMentors(
            @Param("skillIds") List<UUID> skillIds,
            @Param("subjectIds") List<Integer> subjectIds,
            Pageable pageable
    );

    @Query("SELECT c FROM CourseMentor c ORDER BY c.course.createdDate ASC")
    Page<CourseMentor> findAlByOrderByCreatedDateAsc(Pageable pageable);

    @Query("SELECT c FROM CourseMentor c ORDER BY c.course.createdDate DESC")
    Page<CourseMentor> findAlByOrderByCreatedDateDesc(Pageable pageable);

    @Query("SELECT c FROM CourseMentor c ORDER BY c.course.name ASC")
    Page<CourseMentor> findAlByOrderByTitleAsc(Pageable pageable);

    @Query("SELECT c FROM CourseMentor c ORDER BY c.course.name DESC")
    Page<CourseMentor> findAlByOrderByTitleDesc(Pageable pageable);

    List<CourseMentor> findByCourseId(UUID courseMentorId);

    @Query("SELECT cm.course FROM CourseMentor cm WHERE cm.mentor.id = :mentorId")
    List<Course> findCoursesByMentorId(@Param("mentorId") UUID mentorId);
}
