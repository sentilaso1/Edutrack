package com.example.edutrack.common.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAdvice {

    private final EnrollmentService enrollmentService;
    private MentorRepository mentorRepository;

    public GlobalModelAdvice(EnrollmentService enrollmentService, MentorRepository mentorRepository) {
        this.enrollmentService = enrollmentService;
        this.mentorRepository = mentorRepository;
    }

    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user != null) {
            boolean hasEnrollment = !enrollmentService.getEnrollmentsByMenteeId(user.getId()).isEmpty();
            model.addAttribute("hasEnrollment", hasEnrollment);
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("hasEnrollment", false);
            model.addAttribute("isLoggedIn", false);
        }
    }

    @ModelAttribute("isMentor")
    public boolean addGlobalAttributes(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return false;
        }
        return mentorRepository.existsById(loggedInUser.getId());
    }
}