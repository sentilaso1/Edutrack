package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.model.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface GoalService {
    void saveGoal(UUID menteeId, Goal goal);

    List<Goal> getGoalsByMentee(UUID menteeId);

    void updateGoalStatus(UUID goalId, Goal.Status newStatus, UUID menteeId);

    Goal getGoalById(UUID goalId);

    void deleteGoal(UUID goalId);

    Page<Goal> getGoalsByMenteeAndStatus(UUID menteeId, Goal.Status status, Pageable pageable);
}
