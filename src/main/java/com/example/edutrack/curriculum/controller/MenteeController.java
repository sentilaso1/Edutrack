package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.curriculum.dto.*;
import com.example.edutrack.curriculum.model.*;
import com.example.edutrack.curriculum.service.interfaces.*;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
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

import java.time.DayOfWeek;
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
    private final HomeControlller homeControlller;
    private final CourseTagService courseTagService;
    private final FeedbackService feedbackService;

    public MenteeController(FeedbackService feedbackService, DashboardService dashboardService, GoalService goalService, CourseMentorService courseMentorService, EnrollmentService enrollmentService, MenteeRepository menteeRepository, EnrollmentScheduleService enrollmentScheduleService, HomeControlller homeControlller, CourseTagService courseTagService) {
        this.dashboardService = dashboardService;
        this.courseMentorService = courseMentorService;
        this.enrollmentService = enrollmentService;
        this.goalService = goalService;
        this.menteeRepository = menteeRepository;
        this.enrollmentScheduleService = enrollmentScheduleService;
        this.homeControlller = homeControlller;
        this.courseTagService = courseTagService;
        this.feedbackService = feedbackService;
    }

    private UUID getSessionMentee(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return null;
        }
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
        if (menteeId == null) {
            return "redirect:/404";
        }
        homeControlller.addTopTagsToModel(model, 5);
        List<Tag> allCourseTags = courseTagService.getAllTags();
        List<Integer> allCourseTagIds = allCourseTags.stream().map(Tag::getId).toList();
        model.addAttribute("allCourseTagIds", allCourseTagIds);
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
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "mentorId", required = false) UUID mentorId,
            @RequestParam(name = "month", required = false) String month,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "goalStatus", required = false) String goalStatus,
            @RequestParam(name = "editGoalId", required = false) UUID editGoalId,
            @RequestParam(name = "reviewKeyword", required = false) String reviewKeyword,
            @RequestParam(name = "ratingFilter", required = false) Integer ratingFilter,
            Model model
    ) {
        UUID menteeId = getSessionMentee(session);
        if (menteeId == null) return "redirect:/404";

        List<Course> enrolledCourses = enrollmentService.findCourseByMenteeIdAndEnrollmentStatus(menteeId);
        if (enrolledCourses == null || enrolledCourses.isEmpty()) {
            return "redirect:/dashboard";
        }

        YearMonth selectedMonth = null;
        if (month != null && !month.isBlank()) {
            try {
                selectedMonth = YearMonth.parse(month);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TrackerDTO tracker = dashboardService.convertToTrackerDto(menteeId);

        // SKILLS TAB
        if (activeTab == null || activeTab.equals("skills")) {
            Pageable pageable = PageRequest.of(page, size);
            Page<SkillProgressDTO> skillPage = dashboardService.getSkillProgressList(menteeId, keyword, selectedMonth, mentorId, pageable);

            model.addAttribute("skills", skillPage.getContent());
            model.addAttribute("totalPages", skillPage.getTotalPages());
            model.addAttribute("currentPage", page);
            model.addAttribute("keyword", keyword);
            model.addAttribute("selectedMentorId", mentorId);
            model.addAttribute("filterMonth", (selectedMonth != null) ? selectedMonth.toString() : null);
            model.addAttribute("mentorList", enrollmentService.findMentorsByMentee(menteeId));
        }

        // SESSIONS TAB
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
            model.addAttribute("schedules", schedulePage.getContent());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", schedulePage.getTotalPages());
            model.addAttribute("filterMonth", currentMonth.toString());
            model.addAttribute("selectedCourseId", null);
            model.addAttribute("selectedStatus", null);
            model.addAttribute("courseList", enrolledCourses);
        }

        // GOALS TAB
        if ("goals".equals(activeTab)) {
            Pageable goalPageable = PageRequest.of(page, size);
            Goal.Status selectedStatusEnum = null;
            if (goalStatus != null) {
                try {
                    selectedStatusEnum = Goal.Status.valueOf(goalStatus);
                } catch (IllegalArgumentException e) {
                    selectedStatusEnum = null;
                }
            }

            Page<Goal> goalPage = goalService.getGoalsByMenteeAndStatus(menteeId, selectedStatusEnum, goalPageable);
            model.addAttribute("goals", goalPage.getContent());
            model.addAttribute("goalTotalPages", goalPage.getTotalPages());
            model.addAttribute("goalCurrentPage", page);
            model.addAttribute("selectedGoalStatus", goalStatus);
            if (!model.containsAttribute("editGoal")) {
                if (editGoalId != null) {
                    Goal goalToEdit = goalService.getGoalById(editGoalId);
                    if (goalToEdit != null) {
                        model.addAttribute("editGoal", goalToEdit);
                    } else {
                        model.addAttribute("editGoal", new Goal());
                    }
                } else {
                    model.addAttribute("editGoal", new Goal());
                }
            }
            if (!model.containsAttribute("editGoalId")) {
                model.addAttribute("editGoalId", editGoalId);
            }
            model.addAttribute("size", size);

        }

        // Feedback tab
        if ("reviews".equals(activeTab)) {
            Pageable pageable = PageRequest.of(page, size);
            Page<ReviewDTO> reviewPage = feedbackService.getFilteredReviewsByMentee(
                    menteeId,
                    reviewKeyword,
                    ratingFilter,
                    pageable
            );

            model.addAttribute("reviewList", reviewPage.getContent());
            model.addAttribute("reviewPage", reviewPage);
            model.addAttribute("reviewKeyword", reviewKeyword);
            model.addAttribute("ratingFilter", ratingFilter);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", reviewPage.getTotalPages());
        }

        model.addAttribute("progressTracker", tracker);

        if (!model.containsAttribute("newGoal")) {
            model.addAttribute("newGoal", new Goal());
        }

        model.addAttribute("activeTab", activeTab != null ? activeTab : "skills");
        return "mentee/learning-tracker";
    }

    @PostMapping("/goals/create")
    public String createGoal(
            HttpSession session,
            @ModelAttribute("newGoal") @Valid Goal goal,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        UUID menteeId = getSessionMentee(session);
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newGoal", result);
            redirectAttributes.addFlashAttribute("newGoal", goal);
            redirectAttributes.addFlashAttribute("showGoalForm", true);
            return "redirect:/learning-tracker?activeTab=goals";
        }

        goal.setMentee(menteeRepository.findById(menteeId).orElse(null));
        goal.setStatus(Goal.Status.TODO);
        goalService.saveGoal(menteeId, goal);

        return "redirect:/learning-tracker?activeTab=goals";
    }


    @PostMapping("/goals/update-status")
    public String updateGoalStatus(HttpSession session,
                                   @RequestParam("goalId") UUID goalId,
                                   @RequestParam("status") String status) {
        UUID menteeId = getSessionMentee(session);
        goalService.updateGoalStatus(goalId, Goal.Status.valueOf(status), menteeId);
        return "redirect:/learning-tracker?activeTab=goals";
    }

    @PostMapping("/goals/edit")
    public String editGoal(
            HttpSession session,
            @ModelAttribute("editGoal") @Valid Goal goal,
            BindingResult result,
            RedirectAttributes redirectAttributes
    ) {
        UUID menteeId = getSessionMentee(session);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editGoal", result);
            redirectAttributes.addFlashAttribute("editGoal", goal);
            redirectAttributes.addFlashAttribute("editGoalId", goal.getId());
            return "redirect:/learning-tracker?activeTab=goals";
        }

        Goal existingGoal = goalService.getGoalById(goal.getId());
        if (existingGoal == null) {
            return "redirect:/learning-tracker?activeTab=goals";
        }

        existingGoal.setTitle(goal.getTitle());
        existingGoal.setDescription(goal.getDescription());
        existingGoal.setTargetDate(goal.getTargetDate());
        goalService.saveGoal(menteeId, existingGoal);

        return "redirect:/learning-tracker?activeTab=goals";
    }



    @PostMapping("/goals/delete/{id}")
    public String deleteGoal(@PathVariable("id") UUID goalId) {
        goalService.deleteGoal(goalId);
        return "redirect:/learning-tracker?activeTab=goals";
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

    @GetMapping("/schedules")
    public String showSchedulesPage(
            HttpSession session,
            @RequestParam(value = "courseId", required = false) UUID courseId,
            @RequestParam(value = "weekOffset", defaultValue = "0") int weekOffset,
            Model model
    ) {
        UUID menteeId = getSessionMentee(session);
        LocalDate mondayOfWeek = LocalDate.now().plusWeeks(weekOffset).with(java.time.DayOfWeek.MONDAY);

        List<EnrollmentSchedule> schedules;
        if (courseId != null) {
            schedules = enrollmentScheduleService.getEnrollmentSchedulesByCourseAndMentee(courseId, menteeId);
        } else {
            schedules = enrollmentScheduleService.getEnrollmentSchedulesByMentee(menteeId);
        }

        // Lọc schedule theo tuần
        LocalDate start = mondayOfWeek;
        LocalDate end = mondayOfWeek.plusDays(6);
        schedules = schedules.stream()
                .filter(s -> !s.getDate().isBefore(start) && !s.getDate().isAfter(end))
                .toList();
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<ScheduleDTO> scheduleDTOs = enrollmentScheduleService.getScheduleDTOs(schedules, menteeId);
        List<CourseMentor> courses = enrollmentService.getCourseMentorsByMentee(menteeId);
        int testCount = enrollmentScheduleService.countTestSlot(menteeId);
        model.addAttribute("courses", courses);
        model.addAttribute("selectedCourseId", courseId);
        model.addAttribute("slots", Slot.values());
        model.addAttribute("days", List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"));
        model.addAttribute("schedules", scheduleDTOs);
        model.addAttribute("stats", enrollmentScheduleService.getWeeklyStats(menteeId));
        model.addAttribute("upcomingMeeting", dashboardService.getNextSessionTime(menteeId));
        model.addAttribute("weekOffset", weekOffset);
        model.addAttribute("mondayOfWeek", mondayOfWeek);
        model.addAttribute("testCount", testCount);
        model.addAttribute("todayDay", today.name());
        return "mentee/mentee-calendar";
    }


}
