package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.MentorService;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.service.implementation.CourseMentorServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class MentorController {
    private final MentorService mentorService;
    private final CourseMentorServiceImpl courseMentorServiceImpl;

    public MentorController(MentorService mentorService, CourseMentorServiceImpl courseMentorServiceImpl) {
        this.mentorService = mentorService;
        this.courseMentorServiceImpl = courseMentorServiceImpl;
    }

    // View mentors in a list, directory: localhost:port/mentors
    @GetMapping("/mentors")
    public String viewMentorList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String expertise,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) Integer totalSessions,
            @RequestParam(required = false) Boolean isAvailable,
            Model model) {

        List<Mentor> mentorList = mentorService.searchMentors(name, expertise, rating, totalSessions, isAvailable);

        model.addAttribute("mentors", mentorList);
        model.addAttribute("name", name);
        model.addAttribute("expertise", expertise);
        model.addAttribute("rating", rating);
        model.addAttribute("totalSessions", totalSessions);
        model.addAttribute("isAvailable", isAvailable);
        return "mentor-list";
    }

    @GetMapping("/mentor/{id}")
    public String viewMentorDetail(@PathVariable UUID id, Model model){
        List<CourseMentor> courseMentors = courseMentorServiceImpl.getCourseMentorByMentorId(id);

        model.addAttribute("mentor", mentorService.getMentorById(id));
        model.addAttribute("courses", courseMentors);
        model.addAttribute("subjects", mentorService.getTagsByMentor(id));
        model.addAttribute("cv", mentorService.getCVById(id));
        return "mentor-course";
    }
}
