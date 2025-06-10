package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.*;
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
public interface CourseMentorRepository extends JpaRepository<CourseMentor, CourseMentorId> {

    @Query("SELECT ct.tag FROM CourseMentor cm JOIN CourseTag ct ON ct.course = cm.course")
    List<Tag> findAllTags();

    @Query("SELECT cm.course FROM CourseMentor cm")
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

    @Query("SELECT cm FROM CourseMentor cm ORDER BY cm.course.createdDate DESC")
    Page<CourseMentor> findAlByOrderByCreatedDateDesc(
            Pageable pageable
    );

    @Query("SELECT cm FROM CourseMentor cm ORDER BY cm.course.createdDate ASC")
    Page<CourseMentor> findAlByOrderByCreatedDateAsc(
            Pageable pageable
    );

    @Query("SELECT cm FROM CourseMentor cm ORDER BY cm.course.name ASC")
    Page<CourseMentor> findAlByOrderByTitleAsc(
            Pageable pageable
    );

    @Query("SELECT cm FROM CourseMentor cm ORDER BY cm.course.name DESC")
    Page<CourseMentor> findAlByOrderByTitleDesc(
            Pageable pageable
    );

    List<CourseMentor> findByCourseId(UUID courseMentorId);

    @Query("SELECT cm.course FROM CourseMentor cm WHERE cm.mentor.id = :mentorId")
    List<Course> findCoursesByMentorId(@Param("mentorId") UUID mentorId);

    @Query("SELECT cm FROM CourseMentor cm WHERE cm.status = 'ACCEPTED' ORDER BY cm.appliedDate DESC")
    List<CourseMentor> findLatestCourse(Pageable pageable);

    @Query("""
                SELECT DISTINCT cm FROM CourseMentor cm
                JOIN cm.course c
                JOIN CourseTag ct ON ct.course = c
                JOIN Tag t ON ct.tag = t
                WHERE LOWER(t.title) IN :interests
            """)
    List<CourseMentor> findByTagsMatchingInterests(@Param("interests") List<String> interests, Pageable pageable);


    @Query("""
                SELECT DISTINCT cm FROM CourseMentor cm
                JOIN cm.course c
                JOIN CourseTag ct ON ct.course = c
                JOIN ct.tag t
                WHERE LOWER(t.title) IN :tagTitles
                AND cm.course.id NOT IN (
                    SELECT e.courseMentor.course.id FROM Enrollment e
                    WHERE e.mentee.id = :menteeId
                )
            """)
    List<CourseMentor> findRelatedByTagsAndNotEnrolled(
            @Param("tagTitles") List<String> tagTitles,
            @Param("menteeId") UUID menteeId,
            Pageable pageable
    );

    @Query("""
                SELECT cm FROM CourseMentor cm
                JOIN cm.course c
                JOIN CourseTag ct ON ct.course.id = c.id
                WHERE LOWER(ct.tag.title) IN :tags
            """)
    List<CourseMentor> findRelatedByTags(@Param("tags") List<String> tags,
                                         Pageable pageable);

    @Query("""
            SELECT DISTINCT cm FROM CourseMentor cm
            JOIN cm.course c
            JOIN CourseTag ct ON ct.course = c
             WHERE (
                  LOWER(ct.tag.title) IN :keywords
                  OR LOWER(cm.mentor.expertise) IN :keywords
              )
              AND cm.course.id NOT IN (
                  SELECT e.courseMentor.course.id FROM Enrollment e
                  WHERE e.mentee.id = :menteeId
              )
            """)
    List<CourseMentor> findRecommendedByKeywords(
            @Param("keywords") List<String> keywords,
            @Param("menteeId") UUID menteeId,
            Pageable pageable
    );


}
