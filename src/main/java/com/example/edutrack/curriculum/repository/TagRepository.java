package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tags, Integer> {}