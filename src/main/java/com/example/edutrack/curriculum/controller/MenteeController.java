package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.curriculum.dto.CourseCardDTO;
import com.example.edutrack.curriculum.dto.SkillProgressDTO;
import com.example.edutrack.curriculum.dto.TrackerDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Goal;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.curriculum.service.interfaces.DashboardService;
import com.example.edutrack.curriculum.service.interfaces.GoalService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
public class MenteeController {
    private final DashboardService dashboardService;
    private final CourseMentorService courseMentorService;
    private final EnrollmentService enrollmentService;
    private final GoalService goalService;
    private final MenteeRepository menteeRepository;

    public MenteeController(DashboardService dashboardService, GoalService goalService, CourseMentorService courseMentorService, EnrollmentService enrollmentService, MenteeRepository menteeRepository) {
        this.dashboardService = dashboardService;
        this.courseMentorService = courseMentorService;
        this.enrollmentService = enrollmentService;
        this.goalService = goalService;
        this.menteeRepository = menteeRepository;
    }

    private UUID getSessionMentee(HttpSession session){
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        return loggedInUser.getId();
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        UUID menteeId = getSessionMentee(session);
        boolean hasEnrollment = !(enrollmentService.getEnrollmentsByMenteeId(menteeId).isEmpty());
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
    public String showLearningTracker(HttpSession session,Model model){
        UUID menteeId = getSessionMentee(session);
        TrackerDTO progressTracker = dashboardService.convertToTrackerDto(menteeId);
        List<SkillProgressDTO> skillProgressDTOS = dashboardService.getSkillProgressList(menteeId);
        model.addAttribute("skills", skillProgressDTOS);
        List<Goal> goals = goalService.getGoalsByMentee(menteeId);
        model.addAttribute("newGoal", new Goal());
        model.addAttribute("goals", goals);
        model.addAttribute("progressTracker", progressTracker);
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


}
