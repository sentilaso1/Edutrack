package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.curriculum.dto.CourseCardDTO;
import com.example.edutrack.curriculum.dto.SkillProgressDTO;
import com.example.edutrack.curriculum.dto.TrackerDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Goal;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.curriculum.service.interfaces.DashboardService;
import com.example.edutrack.curriculum.service.interfaces.GoalService;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Controller
public class MenteeController {
    private final DashboardService dashboardService;
    private final CourseMentorService courseMentorService;
    private final EnrollmentService enrollmentService;
    private final GoalService goalService;
    private final MenteeRepository menteeRepository;
    private final EnrollmentScheduleService enrollmentScheduleService;

    public MenteeController(DashboardService dashboardService, GoalService goalService, CourseMentorService courseMentorService, EnrollmentService enrollmentService, MenteeRepository menteeRepository, EnrollmentScheduleService enrollmentScheduleService) {
        this.dashboardService = dashboardService;
        this.courseMentorService = courseMentorService;
        this.enrollmentService = enrollmentService;
        this.goalService = goalService;
        this.menteeRepository = menteeRepository;
        this.enrollmentScheduleService = enrollmentScheduleService;
    }

    private UUID getSessionMentee(HttpSession session){
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        return loggedInUser.getId();
    }

    private void addLearningTrackerAttributes(Model model, UUID menteeId) {
        TrackerDTO progressTracker = dashboardService.convertToTrackerDto(menteeId);
        List<SkillProgressDTO> skillProgressDTOS = dashboardService.getSkillProgressList(menteeId);
        List<Goal> goals = goalService.getGoalsByMentee(menteeId);

        model.addAttribute("progressTracker", progressTracker);
        model.addAttribute("skills", skillProgressDTOS);
        model.addAttribute("newGoal", new Goal());
        model.addAttribute("goals", goals);
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        UUID menteeId = getSessionMentee(session);
        model.addAttribute("nextSessionTime", dashboardService.getNextSessionTime(menteeId));
        model.addAttribute("totalMentors", dashboardService.getTotalMentors(menteeId));
        model.addAttribute("learningProgress", dashboardService.getLearningProgress(menteeId));
        List<CourseMentor> recommendedCourses = courseMentorService.getRecommendedCourseMentors(menteeId, 4);
        List<CourseCardDTO> recommendedCoursesToDTO = enrollmentService.mapToCourseCardDTOList(recommendedCourses);
        model.addAttribute("recommendedCourses", recommendedCoursesToDTO);
        model.addAttribute("isAllCompleted", dashboardService.isAllCoursesCompleted(menteeId));
        return "mentee/mentee-dashboard";
    }

    @GetMapping("/learning-tracker")
    public String showLearningTracker(
            HttpSession session,
            @RequestParam(name = "activeTab", required = false) String activeTab,
            Model model) {

        UUID menteeId = getSessionMentee(session);
        addLearningTrackerAttributes(model, menteeId);

        // Nếu là tab attendance thì load schedule
        if ("sessions".equals(activeTab)) {
            LocalDate now = LocalDate.now();
            YearMonth currentMonth = YearMonth.from(now);
            Pageable pageable = PageRequest.of(0, 5);

            Page<EnrollmentSchedule> schedulePage = dashboardService.getFilteredSchedules(
                    menteeId,
                    currentMonth.getMonthValue(),
                    currentMonth.getYear(),
                    null,
                    null,
                    pageable
            );

            List<Course> foundCourseMentor = enrollmentService.findCourseByMenteeIdAndEnrollmentStatus(menteeId);
            model.addAttribute("schedules", schedulePage.getContent());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", schedulePage.getTotalPages());
            model.addAttribute("filterMonth", currentMonth.toString());
            model.addAttribute("selectedCourseId", null);
            model.addAttribute("selectedStatus", null);
            model.addAttribute("courseList", foundCourseMentor);
        }

        model.addAttribute("activeTab", activeTab != null ? activeTab : "skills");
        return "mentee/learning-tracker";
    }


    @PostMapping("/goals/create")
    public String createGoal(
            HttpSession session,
            @ModelAttribute("newGoal") @Valid Goal goal,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            UUID menteeId = getSessionMentee(session);
            model.addAttribute("progressTracker", dashboardService.convertToTrackerDto(menteeId));
            model.addAttribute("skills", dashboardService.getSkillProgressList(menteeId));
            model.addAttribute("goals", goalService.getGoalsByMentee(menteeId));
            model.addAttribute("activeTab", "goals");
            model.addAttribute("showGoalForm", true);
            return "mentee/learning-tracker";
        }
        UUID menteeId = getSessionMentee(session);
        goal.setMentee(menteeRepository.findById(menteeId).orElse(null));
        goal.setStatus(Goal.Status.TODO);
        goalService.saveGoal(menteeId, goal);
        return "redirect:/learning-tracker#goals";
    }

    @PostMapping("/goals/update-status")
    public String updateGoalStatus(HttpSession session,
                                   @RequestParam("goalId") UUID goalId,
                                   @RequestParam("status") String status) {
        UUID menteeId = getSessionMentee(session);
        goalService.updateGoalStatus(goalId, Goal.Status.valueOf(status), menteeId);
        return "redirect:/learning-tracker#goals";
    }


    @PostMapping("/goals/edit")
    public String editGoal(
            HttpSession session,
            @RequestParam("goalId") UUID goalId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("targetDate") String targetDateStr,
            Model model
    ) {
        UUID menteeId = getSessionMentee(session);
        Goal existingGoal = goalService.getGoalById(goalId);
        if (existingGoal == null) {
            return "redirect:/learning-tracker#goals";
        }
        existingGoal.setTitle(title);
        existingGoal.setDescription(description);
        existingGoal.setTargetDate(LocalDate.parse(targetDateStr));
        goalService.saveGoal(menteeId, existingGoal);
        return "redirect:/learning-tracker#goals";
    }


    @PostMapping("/goals/delete/{id}")
    public String deleteGoal(@PathVariable("id") UUID goalId) {
        goalService.deleteGoal(goalId);
        return "redirect:/learning-tracker#goals";
    }

    @GetMapping("/attendance")
    public String showAttendancePage(
            HttpSession session,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) UUID courseId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(name = "activeTab", required = false) String activeTab,
            Model model
    ) {
        UUID menteeId = getSessionMentee(session);

        addLearningTrackerAttributes(model, menteeId);

        LocalDate now = LocalDate.now();
        YearMonth selectedMonth = (month != null && !month.isBlank())
                ? YearMonth.parse(month)
                : YearMonth.from(now);

        Pageable pageable = PageRequest.of(page, 5);

        Page<EnrollmentSchedule> schedulePage = dashboardService.getFilteredSchedules(
                menteeId,
                selectedMonth.getMonthValue(),
                selectedMonth.getYear(),
                courseId,
                status,
                pageable
        );

        List<Course> foundCourseMentor = enrollmentService.findCourseByMenteeIdAndEnrollmentStatus(menteeId);

        model.addAttribute("schedules", schedulePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", schedulePage.getTotalPages());
        model.addAttribute("filterMonth", selectedMonth.toString());
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("courseList", foundCourseMentor);
        model.addAttribute("activeTab", activeTab != null ? activeTab : "sessions");

        return "/mentee/learning-tracker";
    }

    @PostMapping("/report")
    public String submitAttendanceReport(@RequestParam("scheduleId") int scheduleId,
                                         @RequestParam("report") String reportContent,
                                         RedirectAttributes redirectAttributes) {
        EnrollmentSchedule schedule = enrollmentScheduleService.findById(scheduleId);
        if (schedule != null) {
            schedule.setReport(reportContent);
            enrollmentScheduleService.save(schedule);
            redirectAttributes.addFlashAttribute("success", "Report submitted successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Session not found.");
        }
        return "redirect:/learning-tracker?activeTab=sessions";
    }
}
