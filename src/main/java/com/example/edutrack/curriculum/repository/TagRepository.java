package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByTitle(String title);
    List<Tag> findByCourseId(UUID courseId);
}