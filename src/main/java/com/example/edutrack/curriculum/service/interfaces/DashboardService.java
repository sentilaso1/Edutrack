package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.dto.SkillProgressDTO;
import com.example.edutrack.curriculum.dto.TrackerDTO;

import java.util.List;
import java.util.UUID;

public interface DashboardService {
    String getNextSessionTime(UUID menteeId);

    int getTotalMentors(UUID menteeId);

    int getLearningProgress(UUID menteeId);

    boolean isAllCoursesCompleted(UUID menteeId);

    TrackerDTO convertToTrackerDto(UUID menteeI);

    List<SkillProgressDTO> getSkillProgressList(UUID menteeId);
}
