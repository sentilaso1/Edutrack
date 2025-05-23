package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.edutrack.curriculum.model.CourseTagId;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseTagsRepository extends JpaRepository<CourseTag, CourseTagId> {
    List<CourseTag> findByCourseId(UUID courseId);
}
