package com.example.edutrack.timetables.service.interfaces;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.curriculum.dto.ScheduleDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.dto.RequestedSchedule;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public interface EnrollmentScheduleService {
    List<RequestedSchedule> findStartLearningTime(String summary);

    List<EnrollmentSchedule> findAll();

    List<EnrollmentSchedule> findByMentorAndDateBetween(Mentor mentor, LocalDate weekStart, LocalDate weekEnd);

    EnrollmentSchedule findById(Integer esid);

    EnrollmentSchedule findById(int enrollmentScheduleId);

    void save(EnrollmentSchedule schedule);

    void saveEnrollmentSchedule(Enrollment enrollment);

    List<EnrollmentSchedule> findEnrollmentScheduleByEnrollment(Enrollment enrollment);

    Map<String, Integer> getWeeklyStats(UUID menteeId);

    List<EnrollmentSchedule> getEnrollmentSchedulesByCourseAndMentee(UUID courseMentorId, UUID menteeId);

    List<EnrollmentSchedule> getEnrollmentSchedulesByMentee(UUID menteeId);

    int countTestSlot(UUID menteeId);

    List<ScheduleDTO> getScheduleDTOs(List<EnrollmentSchedule> schedules, UUID menteeId);

    boolean submitRescheduleRequest(int scheduleId, Slot newSlot, LocalDate newDate,
                                    String reason, UUID menteeId);

    ScheduleDTO getScheduleDTO(Long scheduleId, UUID menteeId);

    List<ScheduleDTO> getOccupiedSlotsForWeek(UUID menteeId, LocalDate weekStart, LocalDate weekEnd);

    boolean isSlotAvailable(UUID menteeId, Slot newSlot, LocalDate newDate, int scheduleId);

    List<EnrollmentSchedule> getSlotsUnderReview(UUID menteeId, LocalDate startDate, LocalDate endDate);

    boolean resetExpiredRescheduleRequests();
}
