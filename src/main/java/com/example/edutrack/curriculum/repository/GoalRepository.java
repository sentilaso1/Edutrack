package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByMenteeIdOrderByTargetDateAsc(UUID menteeId);

    @Query("SELECT COUNT(g) FROM Goal g WHERE g.mentee.id = :menteeId AND g.status = :status")
    int countCompletedGoalsByMenteeId(@Param("menteeId") UUID menteeId, @Param("status") Goal.Status status);
    Page<Goal> findByMentee_Id(UUID menteeId, Pageable pageable);
    Page<Goal> findByMentee_IdAndStatus(UUID menteeId, Goal.Status status, Pageable pageable);
}