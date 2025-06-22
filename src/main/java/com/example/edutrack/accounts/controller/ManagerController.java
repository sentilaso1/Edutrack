package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.service.interfaces.MenteeService;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManagerController {
    private final MentorService mentorService;
    private final MenteeService menteeService;

    @Autowired
    public ManagerController(MentorService mentorService, MenteeService menteeService) {
        this.mentorService = mentorService;
        this.menteeService = menteeService;
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
}

