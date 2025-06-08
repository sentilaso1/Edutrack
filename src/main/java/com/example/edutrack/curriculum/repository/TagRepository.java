package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByTitle(String title);

    @Query(
            value = "SELECT t.* FROM tags t " +
                    "JOIN course_tags ct ON t.id = ct.tag_id " +
                    "WHERE ct.course_id = :courseId",
            nativeQuery = true
    )
    List<Tag> findByCourseId(@Param("courseId") UUID courseId);


    @Query("""
    SELECT ct.tag FROM CourseMentor cm
    JOIN CourseTag ct ON ct.course = cm.course
    WHERE cm.id = :courseMentorId
""")
    List<Tag> findByCourseMentorId(@Param("courseMentorId") UUID courseMentorId);


    @Query("""
                SELECT DISTINCT t.title FROM Enrollment e
                JOIN e.courseMentor cm
                JOIN cm.course c
                JOIN CourseTag ct ON ct.course = c
                JOIN Tag t ON ct.tag = t
                WHERE e.mentee.id = :menteeId AND e.status = 'ACCEPTED'
            """)
    List<String> findDistinctTagTitlesFromMenteeId(@Param("menteeId") UUID menteeId);

}