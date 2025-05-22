package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.CourseMentorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MentorCourseRepository extends JpaRepository<CourseMentor, CourseMentorId> {
    List<CourseMentor> findByCourseId(UUID courseId);
}