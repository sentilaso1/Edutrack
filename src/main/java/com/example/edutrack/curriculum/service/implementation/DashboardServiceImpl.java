package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.dto.SkillProgressDTO;
import com.example.edutrack.curriculum.dto.TrackerDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    // Hàm F5
    @Override
    public String getNextSessionTime(UUID menteeId) {
        List<EnrollmentSchedule> schedules = enrollmentScheduleRepository.findAllByMenteeId(menteeId);
        if (schedules == null || schedules.isEmpty()) {
            return "No upcoming session";
        }
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

        String formattedTime = nearest.format(DateTimeFormatter.ofPattern("EEEE, hh:mm a", Locale.ENGLISH));
        String courseName = next.getEnrollment().getCourseMentor().getCourse().getName();
        return courseName + " - " + formattedTime;
    }


    @Override
    public int getTotalMentors(UUID menteeId) {
        return mentorRepository.countMentorsByMenteeId(menteeId);
    }

    // Hàm F4
    @Override
    public int getLearningProgress(UUID menteeId) {
        List<Enrollment> enrollments = enrollmentRepository.findAcceptedEnrollmentsByMenteeId(
                menteeId, Enrollment.EnrollmentStatus.APPROVED);

        int total = 0;
        for (Enrollment e : enrollments) {
            total += enrollmentScheduleRepository.countTotalSlot(e.getId());
        }

        if (total == 0) return 0;

        int completed = enrollmentScheduleRepository.countAttendedSlotsByMenteeId(
                menteeId, EnrollmentSchedule.Attendance.PRESENT);


        completed = Math.min(completed, total);

        return (int) Math.round((completed * 100.0) / total);
    }

    @Override
    public Optional<Boolean> hasCompletedCourse(CourseMentor courseMentor, Mentee mentee) {
        List<EnrollmentSchedule> schedules = enrollmentScheduleRepository.findEnrollmentScheduleByMenteeAndCourseMentor(
                mentee.getId(), courseMentor.getId()
        );

        if (schedules.isEmpty()) {
            return Optional.of(true);
        }

        boolean allPresent = schedules.stream()
                .allMatch(s -> s.getAttendance() == EnrollmentSchedule.Attendance.PRESENT && !s.getReport());

        return Optional.of(allPresent);
    }

    @Override
    public Optional<Boolean> hasCompletedCourse(Enrollment enrollment) {
        return this.hasCompletedCourse(enrollment.getCourseMentor(), enrollment.getMentee());
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
        return getSkillProgressList(menteeId, null, null, null);
    }

    @Override
    public Page<SkillProgressDTO> getSkillProgressList(UUID menteeId, String keyword, YearMonth selectedMonth, UUID mentorId, Pageable pageable){
        List<SkillProgressDTO> fullList = getSkillProgressList(menteeId, keyword, selectedMonth, mentorId);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), fullList.size());
        List<SkillProgressDTO> pageContent = (start <= end) ? fullList.subList(start, end) : List.of();
        return new PageImpl<>(pageContent, pageable, fullList.size());
    }

    // Hàm F2
    @Override
    public List<SkillProgressDTO> getSkillProgressList(UUID menteeId, String keyword, YearMonth selectedMonth, UUID mentorId) {
        List<Enrollment> menteeEnrollment = enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED);
        List<SkillProgressDTO> skillProgressList = new ArrayList<>();

        DateTimeFormatter fullDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        for (Enrollment e : menteeEnrollment) {
            CourseMentor courseMentor = e.getCourseMentor();
            Course course = courseMentor.getCourse();
            Mentor mentor = courseMentor.getMentor();

            // --- Keyword filter ---
            if (keyword != null && !keyword.isBlank()) {
                if (!course.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    continue;
                }
            }

            // --- Mentor filter ---
            if (mentorId != null && mentor != null && !mentor.getId().equals(mentorId)) {
                continue;
            }

            // --- Month filter ---
            try {
                String rawStartTime = e.getStartTime().trim();
                LocalDateTime parsedStart;

                if (rawStartTime.matches("^\\d{1,2}:\\d{2}$")) {
                    // Time-only format (e.g., "12:00") → use today's date
                    parsedStart = LocalDateTime.of(LocalDate.now(), LocalTime.parse(rawStartTime));
                } else {
                    // Full datetime string
                    parsedStart = LocalDateTime.parse(rawStartTime, fullDateTimeFormatter);
                }

                if (selectedMonth != null && !YearMonth.from(parsedStart).equals(selectedMonth)) {
                    continue;
                }

            } catch (Exception ex) {
                continue;
            }

            Long enrollmentId = e.getId();
            UUID courseId = course.getId();

            int total = enrollmentScheduleRepository.countTotalSlot(enrollmentId);
            int completed = enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(enrollmentId, EnrollmentSchedule.Attendance.PRESENT);
            int percentage = (total == 0) ? 0 : (int) Math.round((completed * 100.0) / total);

            LocalDate lastSession = enrollmentScheduleRepository.findLastPresentSessionDate(enrollmentId, EnrollmentSchedule.Attendance.PRESENT);
            List<String> tags = tagRepository.findByCourseId(courseId)
                    .stream()
                    .map(Tag::getTitle)
                    .collect(Collectors.toList());

            String mentorName = (mentor != null) ? mentor.getFullName() : "Unknown";
            UUID mentorUuid = (mentor != null) ? mentor.getId() : null;

            SkillProgressDTO dto = new SkillProgressDTO(courseMentor.getCourse().getId(),courseMentor.getId(),
                    course.getName(), lastSession, completed, percentage, tags, total, mentorName, mentorUuid
            );

            skillProgressList.add(dto);
        }

        return skillProgressList;
    }



    @Override
    public Page<EnrollmentSchedule> getFilteredSchedules(
            UUID menteeId,
            int month,
            int year,
            UUID courseId,
            UUID mentorId,
            String status,
            Pageable pageable
    ) {

        EnrollmentSchedule.Attendance statusEnum = null;
        if (status != null && !status.isBlank()) {
            try {
                statusEnum = EnrollmentSchedule.Attendance.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return enrollmentScheduleRepository.findByMenteeAndMonthWithCourseFilter(
                menteeId,
                month,
                year,
                courseId,
                mentorId,
                statusEnum,
                pageable
        );
    }


}
