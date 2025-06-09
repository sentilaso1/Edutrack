package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.CourseTagId;
import com.example.edutrack.curriculum.model.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseTagsRepository extends JpaRepository<CourseTag, CourseTagId> {
    boolean existsByCourseAndTag(Course course, Tag tag);
    void deleteByCourseIdAndTagId(UUID courseId, int tagId);

    @Query("""
        SELECT ct.tag as tag, COUNT(e.id) as enrollmentCount
        FROM CourseTag ct
        JOIN ct.course c
        JOIN c.applications cm
        JOIN Enrollment e ON e.courseMentor = cm
        GROUP BY ct.tag
        ORDER BY COUNT(e.id) DESC
        """)
    List<Object[]> findTopTagsByEnrollment(Pageable pageable);

    @Query("""
    SELECT DISTINCT ct.tag
    FROM CourseTag ct
    WHERE ct.tag.id NOT IN :excludedIds
    ORDER BY FUNCTION('RAND')
    """)
    List<Tag> findRandomTagsExcluding(@Param("excludedIds") List<Integer> excludedIds, Pageable pageable);

    @Query("SELECT DISTINCT ct.tag FROM CourseTag ct")
    List<Tag> findDistinctTagsFromCourses();


}
