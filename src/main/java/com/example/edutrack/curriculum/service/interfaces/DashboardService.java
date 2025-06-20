package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.dto.SkillProgressDTO;
import com.example.edutrack.curriculum.dto.TrackerDTO;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DashboardService {
    String getNextSessionTime(UUID menteeId);

    int getTotalMentors(UUID menteeId);

    int getLearningProgress(UUID menteeId);

    boolean isAllCoursesCompleted(UUID menteeId);

    TrackerDTO convertToTrackerDto(UUID menteeI);

    List<SkillProgressDTO> getSkillProgressList(UUID menteeId);

    Page<EnrollmentSchedule> getFilteredSchedules(
            UUID menteeId,
            int month,
            int year,
            UUID courseId,
            String status,
            Pageable pageable
    );
}
