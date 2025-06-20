package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.dto.SkillProgressDTO;
import com.example.edutrack.curriculum.dto.TrackerDTO;
import com.example.edutrack.curriculum.model.Goal;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.GoalRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.interfaces.DashboardService;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.repository.EnrollmentRepository;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final EnrollmentScheduleRepository enrollmentScheduleRepository;
    private final MentorRepository mentorRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TagRepository tagRepository;
    private final GoalRepository goalRepository;


    public DashboardServiceImpl(EnrollmentScheduleRepository enrollmentScheduleRepository, MentorRepository mentorRepository, EnrollmentRepository enrollmentRepository, TagRepository tagRepository, GoalRepository goalRepository) {
        this.enrollmentScheduleRepository = enrollmentScheduleRepository;
        this.mentorRepository = mentorRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.tagRepository = tagRepository;
        this.goalRepository = goalRepository;
    }

    @Override
    public String getNextSessionTime(UUID menteeId) {
        List<EnrollmentSchedule> schedules = enrollmentScheduleRepository.findAllByMenteeId(menteeId);

        EnrollmentSchedule next = null;
        LocalDateTime nearest = null;

        for (EnrollmentSchedule s : schedules) {
            //Temporarily use structure date in EnrollmentSchedule is yyyy:mm:dd and slot is hh:mm:ss
            LocalDate date = s.getDate();
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
        List<Enrollment> enrollments = enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED);
        int total = 0;
        for (Enrollment e : enrollments) {
            total += e.getTotalSlots();
        }
        int completed = enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT);
        if (total == 0) return 0;
        return (int) Math.round((completed * 100.0) / total);
    }

    @Override
    public boolean isAllCoursesCompleted(UUID menteeId) {
        return enrollmentScheduleRepository.countUnfinishedSlotsByMentee(menteeId, EnrollmentSchedule.Attendance.PRESENT) == 0;
    }

    @Override
    public TrackerDTO convertToTrackerDto(UUID menteeId){
        int learningProgres = getLearningProgress(menteeId);
        int totalTrackedSkills = (enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED)).size();
        int completedGoals = goalRepository.countCompletedGoalsByMenteeId(menteeId, Goal.Status.DONE);
        int totalGoals = goalRepository.findByMenteeIdOrderByTargetDateAsc(menteeId).size();
        String goalsCompleted = completedGoals + "/" + totalGoals;
        int skillsCompleted = enrollmentScheduleRepository.countCompletedCourseByMentee(menteeId);

        int percent = (totalGoals == 0) ? 0 : (completedGoals * 100) / totalGoals;
        return new TrackerDTO(learningProgres, goalsCompleted, totalTrackedSkills, skillsCompleted, percent);
    }


    @Override
    public List<SkillProgressDTO> getSkillProgressList(UUID menteeId) {
        List<Enrollment> menteeEnrollment = enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED);
        List<SkillProgressDTO> skillProgressList = new ArrayList<>();

        for (Enrollment e : menteeEnrollment) {
            Long enrollmentId = e.getId();
            UUID courseId = e.getCourseMentor().getCourse().getId();
            int total = e.getTotalSlots();
            int completed = enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(enrollmentId, EnrollmentSchedule.Attendance.PRESENT);
            int percentage = (total == 0) ? 0 : (int) Math.round((completed * 100.0) / total);
            LocalDate lastSession = enrollmentScheduleRepository.findLastPresentSessionDate(enrollmentId, EnrollmentSchedule.Attendance.PRESENT);
            List<String> tags = tagRepository.findByCourseId(courseId).stream().map(Tag::getTitle).collect(Collectors.toList());
            SkillProgressDTO skillProgress = new SkillProgressDTO(e.getCourseMentor().getCourse().getDescription(), lastSession, completed, percentage, tags, total);
            skillProgressList.add(skillProgress);
        }
        return skillProgressList;
    }

    @Override
    public Page<EnrollmentSchedule> getFilteredSchedules(
            UUID menteeId,
            int month,
            int year,
            UUID courseId,
            String status,
            Pageable pageable
    ) {

        Page<EnrollmentSchedule> rawPage = enrollmentScheduleRepository
                .findByMenteeAndMonthWithCourseFilter(menteeId, month, year, courseId, pageable);

        List<EnrollmentSchedule> filteredList = new ArrayList<>();

        for (EnrollmentSchedule schedule : rawPage.getContent()) {
            boolean match = true;

            if (status != null && !status.isBlank()) {
                match = schedule.getAttendance().name().equalsIgnoreCase(status);
            }

            if (match) {
                filteredList.add(schedule);
            }
        }

        filteredList.sort(Comparator
                .comparing(EnrollmentSchedule::getDate)
                .thenComparing(s -> s.getSlot().getStartTime()));

        return new PageImpl<>(filteredList, pageable, rawPage.getTotalElements());
    }


}
