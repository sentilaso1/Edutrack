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

}