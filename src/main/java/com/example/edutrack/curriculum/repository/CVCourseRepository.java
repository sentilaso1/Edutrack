package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.CVCourse;
import com.example.edutrack.curriculum.model.CVCourseId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CVCourseRepository extends JpaRepository<CVCourse, CVCourseId> {
    List<CVCourse> findByIdCvId(UUID cvId);
}
