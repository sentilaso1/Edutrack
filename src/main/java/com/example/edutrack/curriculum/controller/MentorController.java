package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.MentorService;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.service.implementation.CourseMentorServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller(value = "mentee")
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
            @RequestParam(required = false, defaultValue = "") String[] expertise,
            @RequestParam(required = false) Double rating,
            @RequestParam(required = false) Integer totalSessions,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size_page,
            @RequestParam(required = false) String order_by,
            Model model) {

        if (page < 1) {
            return "redirect:/404";
        }
        Sort sort;
        if ("newest".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.DESC, "createdDate");
        } else if ("oldest".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.ASC, "createdDate");
        } else if ("name_asc".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.ASC, "fullName");
        } else if ("name_desc".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.DESC, "fullName");
        } else if ("rating_desc".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.DESC, "rating");
        } else if ("rating_asc".equalsIgnoreCase(order_by)) {
            sort = Sort.by(Sort.Direction.ASC, "rating");
        } else {
            sort = Sort.unsorted();
        }

        Pageable pageable = PageRequest.of(page - 1, size_page, sort);

        // Now, assuming your mentorService supports pagination:
        Page<Mentor> mentorPage = mentorService.searchMentors(
                name, expertise, rating, totalSessions, isAvailable, pageable
        );

        model.addAttribute("mentorPage", mentorPage);
        model.addAttribute("page", page);
        model.addAttribute("name", name);
        model.addAttribute("expertise", expertise);
        model.addAttribute("rating", rating);
        model.addAttribute("totalSessions", totalSessions);
        model.addAttribute("expertiseInput", String.join(", ", expertise));
        model.addAttribute("isAvailable", isAvailable);
        model.addAttribute("order_by", order_by);

        return "mentee/mentor-list";
    }

    @GetMapping("/mentors/{id}")
    public String viewMentorDetail(@PathVariable UUID id, Model model){
        List<CourseMentor> courseMentors = courseMentorServiceImpl.getCourseMentorByMentorId(id);

        model.addAttribute("mentor", mentorService.getMentorById(id));
        model.addAttribute("courses", courseMentors);
        model.addAttribute("subjects", mentorService.getTagsByMentor(id));
        model.addAttribute("cv", mentorService.getCVById(id));
        return "mentor-course";
    }
}
