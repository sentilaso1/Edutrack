package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByMenteeIdOrderByTargetDateAsc(UUID menteeId);
}