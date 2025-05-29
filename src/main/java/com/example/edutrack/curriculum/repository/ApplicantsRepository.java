package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.CourseMentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicantsRepository extends JpaRepository<CourseMentor, UUID> {
    void deleteById(UUID id);
}
