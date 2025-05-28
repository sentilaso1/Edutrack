package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseTag;
import com.example.edutrack.curriculum.model.CourseTagId;
import com.example.edutrack.curriculum.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseTagsRepository extends JpaRepository<CourseTag, CourseTagId> {
    boolean existsByCourseAndTag(Course course, Tag tag);
    void deleteByCourseIdAndTagId(UUID courseId, int tagId);
}
