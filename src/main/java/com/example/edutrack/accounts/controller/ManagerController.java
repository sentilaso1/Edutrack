package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.service.interfaces.MenteeService;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import com.example.edutrack.timetables.dto.MentorAvailableTimeDTO;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.service.implementation.EnrollmentServiceImpl;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ManagerController {
    private final MentorService mentorService;
    private final MenteeService menteeService;
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final EnrollmentService enrollmentService;

    @Autowired
    public ManagerController(MentorService mentorService, MenteeService menteeService, MentorAvailableTimeService mentorAvailableTimeService, EnrollmentService enrollmentServiceImpl) {
        this.mentorService = mentorService;
        this.menteeService = menteeService;
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.enrollmentService = enrollmentServiceImpl;
    }

    @GetMapping("/manager/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("mentorCount", mentorService.countAll());
        model.addAttribute("menteeCount", menteeService.countAll());

        return "manager/dashboard";
    }

    @GetMapping("/manager/mentees")
    public String showMentees(Model model) {
        model.addAttribute("mentees", menteeService.findAll());
        return "manager/mentees";
    }

    @GetMapping("/manager/mentors")
    public String showMentors(Model model) {
        model.addAttribute("mentors", mentorService.findAll());
        return "manager/mentors";
    }

    @GetMapping("/manager/stats")
    public String showStats(Model model) {
        return "accounts/html/manager-stats";
    }

    @GetMapping("/manager/mentor-working-date")
    public String showMentorWorkingDate(Model model) {
        List<MentorAvailableTimeDTO> mentorRequest = mentorAvailableTimeService.findAllDistinctStartEndDates();
        model.addAttribute("mentorRequest", mentorRequest);
        return "manager/working-date-review";
    }

    @GetMapping("/manager/create-class")
    public String showCreateClass(Model model) {
        List<Enrollment> enrollmentRequests = enrollmentService.findAllApprovedEnrollments();
        model.addAttribute("enrollmentRequests", enrollmentRequests);
        return "manager/create-class";
    }
}

