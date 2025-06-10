package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.service.interfaces.DashboardService;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
import com.example.edutrack.timetables.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EnrollmentScheduleRepository enrollmentScheduleRepository;
    private final MentorRepository mentorRepository;
    private final ScheduleRepository scheduleRepository;

    public DashboardServiceImpl(EnrollmentScheduleRepository enrollmentRepository, MentorRepository mentorRepository, ScheduleRepository scheduleRepository) {
        this.enrollmentScheduleRepository= enrollmentRepository;
        this.mentorRepository = mentorRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public String getNextSessionTime(UUID menteeId) {
        List<EnrollmentSchedule> schedules = enrollmentScheduleRepository.findAllByMenteeId(menteeId);

        EnrollmentSchedule next = null;
        LocalDateTime nearest = null;

        for (EnrollmentSchedule s : schedules) {
            //Temporarily use structure date in EnrollmentSchedule is yyyy:mm:dd and slot is hh:mm:ss
            LocalDate date = s.getDate().toInstant().atZone(java.time.ZoneOffset.systemDefault()).toLocalDate();
            LocalTime time = s.getSlot().getStartTime();
            LocalDateTime sessionTime = LocalDateTime.of(date, time);

            if (sessionTime.isAfter(LocalDateTime.now())) {
                if (nearest == null || sessionTime.isBefore(nearest)) {
                    nearest = sessionTime;
                    next = s;
                }
            }
        }

        if (next == null) return "No upcoming session";

        String formattedTime = nearest.format(DateTimeFormatter.ofPattern("EEEE, hh:mm a"));
        String courseName = next.getEnrollment().getCourseMentor().getCourse().getName();

        return courseName + " - " + formattedTime;
    }


    @Override
    public int getTotalMentors(UUID menteeId) {
        return mentorRepository.countMentorsByMenteeId(menteeId);
    }

    @Override
    public int getLearningProgress(UUID menteeId) {
        int total = enrollmentScheduleRepository.countTotalSlotsByMenteeId(menteeId);
        int completed = scheduleRepository.countCompletedAttendanceByMenteeId(menteeId);
        if (total == 0) return 0;
        return (int) Math.round((completed * 100.0) / total);
    }

}
