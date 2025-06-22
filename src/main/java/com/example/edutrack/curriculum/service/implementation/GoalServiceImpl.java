package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.curriculum.model.Goal;
import com.example.edutrack.curriculum.repository.GoalRepository;
import com.example.edutrack.curriculum.service.interfaces.GoalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GoalServiceImpl implements GoalService {
    private final MenteeRepository menteeRepository;
    private final GoalRepository goalRepository;

    public GoalServiceImpl(MenteeRepository menteeRepository, GoalRepository goalRepository) {
        this.menteeRepository = menteeRepository;
        this.goalRepository = goalRepository;
    }

    @Override
    public void saveGoal(UUID menteeId, Goal goal) {
        Mentee mentee = menteeRepository.findById(menteeId).orElseThrow();
        goal.setMentee(mentee);
        if (goal.getStatus() == null) {
            goal.setStatus(Goal.Status.TODO);
        }
        goalRepository.save(goal);
    }

    @Override
    public List<Goal> getGoalsByMentee(UUID menteeId) {
        return goalRepository.findByMenteeIdOrderByTargetDateAsc(menteeId);
    }

    @Override
    public void updateGoalStatus(UUID goalId, Goal.Status newStatus, UUID menteeId) {
        Goal goal = goalRepository.findById(goalId)
                .filter(g -> g.getMentee().getId().equals(menteeId))
                .orElseThrow(() -> new IllegalArgumentException("Invalid goal"));

        goal.setStatus(newStatus);
        goalRepository.save(goal);
    }

    @Override
    public Goal getGoalById(UUID goalId) {
        return goalRepository.findById(goalId).orElseThrow(() -> new IllegalArgumentException("Invalid goal"));
    }

    @Override
    public void deleteGoal(UUID goalId){
        Goal goal = goalRepository.findById(goalId).orElseThrow(() -> new IllegalArgumentException("Invalid goal"));
        goalRepository.delete(goal);
    }

    @Override
    public Page<Goal> getGoalsByMenteeAndStatus(UUID menteeId, Goal.Status status, Pageable pageable) {
        if (status != null) {
            return goalRepository.findByMentee_IdAndStatus(menteeId, status, pageable);
        } else {
            return goalRepository.findByMentee_Id(menteeId, pageable);
        }
    }

}
