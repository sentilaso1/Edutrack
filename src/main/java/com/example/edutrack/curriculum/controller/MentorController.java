package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.service.MentorService;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.curriculum.service.implementation.CourseMentorServiceImpl;
import com.example.edutrack.curriculum.service.interfaces.FeedbackService;
import jakarta.servlet.http.HttpSession;
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
import java.util.Optional;
import java.util.UUID;

@Controller(value = "mentee")
public class MentorController {
    private final MentorService mentorService;
    private final CourseMentorServiceImpl courseMentorServiceImpl;
    private final FeedbackService feedbackService;
    private final CourseRepository courseRepository;

    public MentorController(MentorService mentorService,
                            CourseMentorServiceImpl courseMentorServiceImpl,
                            FeedbackService feedbackService,
                            CourseRepository courseRepository) {
        this.mentorService = mentorService;
        this.courseMentorServiceImpl = courseMentorServiceImpl;
        this.feedbackService = feedbackService;
        this.courseRepository = courseRepository;
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

        List<Feedback> feedbacks = feedbackService.getAllFeedbacksByMentorId(id);

        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("mentor", mentorService.getMentorById(id));
        model.addAttribute("courses", courseMentors);
        model.addAttribute("subjects", mentorService.getTagsByMentor(id));
        model.addAttribute("cv", mentorService.getCVById(id));
        return "mentor-course";
    }

    @GetMapping("/mentors/{mentorId}/review")
    public String showReviewForm(@PathVariable UUID mentorId,
                                 @RequestParam UUID courseId,
                                 HttpSession session,
                                 Model model) {
        Optional<Mentor> mentorOpt = mentorService.getMentorById(mentorId);

        if (mentorOpt.isPresent()) {
            Mentor mentor = mentorOpt.get();
            model.addAttribute("mentor", mentor);
        }

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            model.addAttribute("course", course);
        }

        Mentee mentee = (Mentee) session.getAttribute("loggedInUser");

        UUID menteeId = mentee.getId();
        model.addAttribute("menteeId", menteeId);
        model.addAttribute("courseId", courseId);

        return "mentee/mentor-review";
    }
}
