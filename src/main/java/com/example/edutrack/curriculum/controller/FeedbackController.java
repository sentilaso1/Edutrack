package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.curriculum.dto.FeedbackDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.CourseMentorId;
import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.FeedbackRepository;
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
    private final FeedbackRepository feedbackRepository;

    public FeedbackController(FeedbackService feedbackService,
                              MenteeRepository menteeRepository,
                              CourseMentorRepository courseMentorRepository,
                              CourseMentorService courseMentorService, FeedbackRepository feedbackRepository) {
        this.feedbackService = feedbackService;
        this.menteeRepository = menteeRepository;
        this.courseMentorRepository = courseMentorRepository;
        this.courseMentorService = courseMentorService;
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping("/reviews")
    public String showReviewList(HttpSession session, Model model) {
        Mentee mentee = (Mentee) session.getAttribute("loggedInUser");
        if (mentee == null) {
            return "redirect:/login";
        }
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

            Double rating = request.getRating();
            if (rating == null || rating < 0 || rating > 5) {
                redirectAttributes.addFlashAttribute("error", "Rating must be between 0 and 5");
                return "redirect:/mentors/" + request.getMentorId();
            }

            String content = request.getContent();
            if (content == null || content.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Content must not be empty");
                return "redirect:/mentors/" + request.getMentorId();
            }
            if (content.length() > 3000) {
                redirectAttributes.addFlashAttribute("error", "Content is too long");
                return "redirect:/mentors/" + request.getMentorId();
            }

            feedbackService.submitFeedback(
                    request.getContent(),
                    request.getRating(),
                    request.isAnonymous(),
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

}
