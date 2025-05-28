package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.CourseMentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseMentorRepository extends JpaRepository<CourseMentor, UUID> {
    List<CourseMentor> findByCourseId(UUID courseId);

}
