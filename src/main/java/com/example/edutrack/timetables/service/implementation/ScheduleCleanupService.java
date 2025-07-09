package com.example.edutrack.timetables.service.implementation;

import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleCleanupService {

    private final EnrollmentScheduleRepository enrollmentScheduleRepository;

    public ScheduleCleanupService(EnrollmentScheduleRepository enrollmentScheduleRepository) {
        this.enrollmentScheduleRepository = enrollmentScheduleRepository;
    }

    @Scheduled(cron = "0 0 2 * * ?") // Runs at 2 AM every day
    @Transactional
    public void processExpiredRescheduleRequests() {
        LocalDate today = LocalDate.now();
        List<EnrollmentSchedule> pendingRequests = enrollmentScheduleRepository.findAllByRescheduleStatus(EnrollmentSchedule.RescheduleStatus.REQUESTED);

        for (EnrollmentSchedule schedule : pendingRequests) {
            boolean updated = false;

            // Scenario 1: Requested date has passed, but the original date is still in the future -> Reset request
            if (schedule.getRequestedNewDate() != null && schedule.getRequestedNewDate().isBefore(today) && schedule.getDate().isAfter(today.minusDays(1))) {
                schedule.setRescheduleStatus(EnrollmentSchedule.RescheduleStatus.NONE);
                schedule.setRescheduleReason("AUTO_CANCELED_EXPIRED_REQUESTED_DATE");
                updated = true;
            }
            // Scenario 2: Original session date has passed -> Mark as absent
            else if (schedule.getDate().isBefore(today)) {
                schedule.setRescheduleStatus(EnrollmentSchedule.RescheduleStatus.REJECTED);
                schedule.setAttendance(EnrollmentSchedule.Attendance.ABSENT);
                schedule.setAvailable(false);
                schedule.setRescheduleReason("AUTO_REJECTED_EXPIRED_ORIGINAL_DATE");
                updated = true;
            }

            // If an update occurred, set the timestamp
            if (updated) {
                schedule.setRescheduleStatusUpdateDate(LocalDateTime.now());
                enrollmentScheduleRepository.save(schedule);
            }
        }
    }
}