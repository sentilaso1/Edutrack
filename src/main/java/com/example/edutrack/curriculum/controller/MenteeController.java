package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.curriculum.dto.CourseCardDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.curriculum.service.interfaces.DashboardService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class MenteeController {
    private final DashboardService dashboardService;
    private final CourseMentorService courseMentorService;
    private final EnrollmentService enrollmentService;

    public MenteeController(DashboardService dashboardService, CourseMentorService courseMentorService, EnrollmentService enrollmentService) {
        this.dashboardService = dashboardService;
        this.courseMentorService = courseMentorService;
        this.enrollmentService = enrollmentService;
    }
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        UUID menteeId = loggedInUser.getId();
        model.addAttribute("nextSessionTime", dashboardService.getNextSessionTime(menteeId));
        model.addAttribute("totalMentors", dashboardService.getTotalMentors(menteeId));
        model.addAttribute("learningProgress", dashboardService.getLearningProgress(menteeId));
        List<CourseMentor> recommendedCourses = courseMentorService.getRecommendedCourseMentors(menteeId, 4);
        List<CourseCardDTO> recommendedCoursesToDTO = enrollmentService.mapToCourseCardDTOList(recommendedCourses);
        model.addAttribute("recommendedCourses", recommendedCoursesToDTO);
        return "mentee/mentee-dashboard";
    }
}
