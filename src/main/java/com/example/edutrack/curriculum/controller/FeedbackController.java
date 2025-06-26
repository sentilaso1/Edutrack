package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.curriculum.dto.FeedbackDTO;
import com.example.edutrack.curriculum.dto.FeedbackFilterForm;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.CourseMentorId;
import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.curriculum.service.interfaces.FeedbackService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class FeedbackController {
    public static final int PAGE_SIZE = 15;

    private final FeedbackService feedbackService;
    private final MenteeRepository menteeRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final CourseMentorService courseMentorService;

    public FeedbackController(FeedbackService feedbackService,
                              MenteeRepository menteeRepository,
                              CourseMentorRepository courseMentorRepository,
                              CourseMentorService courseMentorService) {
        this.feedbackService = feedbackService;
        this.menteeRepository = menteeRepository;
        this.courseMentorRepository = courseMentorRepository;
        this.courseMentorService = courseMentorService;
    }

    @GetMapping("/reviews")
    public String showReviewList(HttpSession session, Model model) {
        if (session == null) {
            return "redirect:/login";
        }
        Mentee mentee = (Mentee) session.getAttribute("loggedInUser");
        UUID menteeId = mentee.getId();
        List<CourseMentor> reviewPairs = courseMentorService.getReviewablePairsForMentee(menteeId);
        model.addAttribute("reviewPairs", reviewPairs);
        return "review-list";
    }

    @PostMapping("/api/feedback")
    public String submitFeedback(FeedbackDTO request, RedirectAttributes redirectAttributes) {
        try {
            Mentee mentee = menteeRepository.findById(request.getMenteeId())
                    .orElseThrow(() -> new IllegalArgumentException("Mentee not found"));

            CourseMentor courseMentor = courseMentorRepository
                    .findByCourse_IdAndMentor_Id(request.getCourseId(), request.getMentorId())
                    .orElseThrow(() -> new IllegalArgumentException("CourseMentor not found"));

            feedbackService.submitFeedback(
                    request.getContent(),
                    request.getRating(),
                    mentee,
                    courseMentor
            );
            redirectAttributes.addFlashAttribute("success", "Your review was submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not submit review: " + e.getMessage());
        }
        // Redirect to mentor profile page
        return "redirect:/mentors/" + request.getMentorId();
    }

    @GetMapping("/admin/reviews/list/{page}")
    public String listFeedbacks(@ModelAttribute FeedbackFilterForm params, Model model, @PathVariable int page) {
        if (page - 1 < 0) {
            return "redirect:/404";
        }

        String filter = params.getFilter();
        String sort = params.getSort();
        String status = params.getStatus();

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);

        Page<Feedback> feedbackPage = feedbackService.queryFeedbacks(filter, sort, status, pageable);

        model.addAttribute("pageNumber", page);
        model.addAttribute("page", feedbackPage);

        if (sort != null) model.addAttribute("sort", sort);
        if (filter != null) model.addAttribute("filter", filter);
        if (status != null) model.addAttribute("status", status);

        if (feedbackPage.getTotalPages() > 0 && page > feedbackPage.getTotalPages()) {
            return "redirect:/404";
        }

        return "manager/list-feedback";
    }

    @GetMapping("/admin/reviews/list")
    public String redirectToListFeedbacks() {
        return "redirect:/admin/reviews/list/1";
    }

    @GetMapping("/admin/reviews/delete/{id}")
    public String deleteFeedback(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            feedbackService.deleteFeedbackById(id);
            redirectAttributes.addFlashAttribute("success", "Feedback deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete feedback: " + e.getMessage());
        }
        return "redirect:/admin/reviews/list/1";
    }

    // TOGGLE STATUS
    @GetMapping("/admin/reviews/toggle-status/{id}")
    public String toggleFeedbackStatus(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
                feedbackService.toggleFeedbackStatus(id);
            redirectAttributes.addFlashAttribute("success", "Feedback status toggled!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to toggle status: " + e.getMessage());
        }
        return "redirect:/admin/reviews/list/1";
    }
}
