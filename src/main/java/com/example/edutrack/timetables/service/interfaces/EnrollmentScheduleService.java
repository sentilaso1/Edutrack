package com.example.edutrack.timetables.service.interfaces;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.dto.ScheduleActivityBannerDTO;
import com.example.edutrack.timetables.dto.ScheduleDTO;
import com.example.edutrack.timetables.dto.EnrollmentAttendanceDTO;
import com.example.edutrack.timetables.dto.RequestedSchedule;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<ScheduleDTO> getScheduleDTOs(List<EnrollmentSchedule> schedules, UUID menteeId);

    int countTestSlot(UUID menteeId);

    boolean submitRescheduleRequest(int scheduleId, Slot newSlot, LocalDate newDate,
                                    String reason, UUID menteeId);

    ScheduleDTO getScheduleDTO(Long scheduleId, UUID menteeId);

    List<ScheduleDTO> getOccupiedSlotsForWeek(UUID menteeId, LocalDate weekStart, LocalDate weekEnd);

    boolean isSlotAvailable(UUID menteeId, Slot newSlot, LocalDate newDate, int scheduleId);

    List<EnrollmentSchedule> getSlotsUnderReview(UUID menteeId, LocalDate startDate, LocalDate endDate);

    List<EnrollmentSchedule> getSlotsUnderReviewByCourse(UUID menteeId, UUID courseMentorId, LocalDate startDate, LocalDate endDate);

    List<ScheduleActivityBannerDTO> collectRecentActivityBanners(UUID menteeId);

    Long countEnrollmentSchedulesHaveRescheduleRequest(Enrollment enrollment);

    List<EnrollmentSchedule> getPendingRequestsForMentor(UUID mentorId);

    @Transactional
    void approveRescheduleRequest(int scheduleId);

    @Transactional
    void rejectRescheduleRequest(int scheduleId, String reason);

    Page<EnrollmentAttendanceDTO> findAllSchedulesToBeConfirmed(Pageable pageable);

    Page<EnrollmentAttendanceDTO> findAllSchedulesToBeConfirmedFiltered(Pageable pageable, String menteeId, String mentorId);

    Page<EnrollmentSchedule> findScheduleByEnrollment(Long enrollmentId, Pageable pageable);

    Page<EnrollmentSchedule> findScheduleByEnrollmentWithFilters(Long enrollmentId, String attendanceStatus, String slot, Pageable pageable);
}
