package com.example.edutrack.curriculum.service.interfaces;

import java.util.UUID;

public interface DashboardService {
    String getNextSessionTime(UUID menteeId);

    int getTotalMentors(UUID menteeId);

    int getLearningProgress(UUID menteeId);

    boolean isAllCoursesCompleted(UUID menteeId);
}
