package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.curriculum.dto.SkillProgressDTO;
import com.example.edutrack.curriculum.dto.TrackerDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DashboardService {
    String getNextSessionTime(UUID menteeId);

    int getTotalMentors(UUID menteeId);

    int getLearningProgress(UUID menteeId);

    boolean isAllCoursesCompleted(UUID menteeId);

    Optional<Boolean> hasCompletedCourse(CourseMentor courseMentor, Mentee mentee);

    Optional<Boolean> hasCompletedCourse(Enrollment enrollment);

    TrackerDTO convertToTrackerDto(UUID menteeI);

    List<SkillProgressDTO> getSkillProgressList(UUID menteeId);

    Page<SkillProgressDTO> getSkillProgressList(UUID menteeId, String keyword, YearMonth selectedMonth, UUID mentorId, Pageable pageable);

    List<SkillProgressDTO> getSkillProgressList(UUID menteeId, String keyword, YearMonth selectedMonth, UUID mentorId);

    Page<EnrollmentSchedule> getFilteredSchedules(
            UUID menteeId,
            int month,
            int year,
            UUID courseId,
            String status,
            Pageable pageable
    );
}
