package com.example.edutrack.common.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.service.implementation.EnrollmentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class MentorModelAdvice {

    private final EnrollmentServiceImpl enrollmentService;

    public MentorModelAdvice(EnrollmentServiceImpl enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @ModelAttribute("numberOfPending")
    public Integer numberOfPending(HttpServletRequest request, HttpSession session) {
        User mentor = (User) session.getAttribute("loggedInUser");
        String path = request.getRequestURI();

        if (path.startsWith("/mentor")) {
            return enrollmentService.findByStatusAndMentor(Enrollment.EnrollmentStatus.PENDING, mentor.getId()).size();
        }

        return null;
    }
}
