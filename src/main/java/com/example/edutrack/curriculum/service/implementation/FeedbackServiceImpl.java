package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.common.service.interfaces.LLMService;
import com.example.edutrack.curriculum.dto.ReviewDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.FeedbackRepository;
import com.example.edutrack.curriculum.service.interfaces.FeedbackService;
import com.example.edutrack.profiles.model.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final LLMService llmService;
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository,
                               CourseMentorRepository courseMentorRepository,
                               LLMService llmService) {
        this.feedbackRepository = feedbackRepository;
        this.courseMentorRepository = courseMentorRepository;
        this.llmService = llmService;
    }

    @Override
    public Page<ReviewDTO> getFilteredReviewsByMentee(UUID menteeId, String keyword, Integer rating, Pageable pageable) {
        Page<Feedback> feedbackPage = feedbackRepository.findFilteredFeedbacksByMentee(menteeId, keyword, rating, pageable);
        return feedbackPage.map(f -> new ReviewDTO(
                f.getCourseMentor().getCourse().getName(),
                f.getCourseMentor().getMentor().getFullName(),
                f.getContent(),
                f.getRating(),
                f.getCreatedDate()
        ));
    }

    @Override
    public Feedback submitFeedback(String content, Double rating, Mentee mentee, CourseMentor courseMentor) {
        if (feedbackRepository.findByMenteeAndCourseMentor(mentee, courseMentor).isPresent()) {
            throw new IllegalStateException("Feedback already submitted for this course and mentor.");
        }
        Feedback feedback = new Feedback(content, rating, mentee, courseMentor);
        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> getAllFeedbacksByMentorId(UUID mentorId) {
        List<CourseMentor> courseMentors = courseMentorRepository.findByMentorId(mentorId);

        List<Feedback> feedbacks = new ArrayList<>();
        for (CourseMentor cm : courseMentors) {
            feedbacks.addAll(feedbackRepository.findAllByCourseMentor(cm));
        }
        return feedbacks;
    }

    @Override
    public Page<Feedback> queryFeedbacks(String filter, String sort, String status, Pageable pageable) {
        Feedback.Status statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = Feedback.Status.valueOf(status);
            } catch (IllegalArgumentException ex) {
                statusEnum = null;
            }
        }

        if (statusEnum != null && filter != null && !filter.isEmpty()) {
            return feedbackRepository.findByStatusAndContentContainingIgnoreCaseOrStatusAndMentee_FullNameContainingIgnoreCase(
                    statusEnum, filter, statusEnum, filter, pageable
            );
        } else if (statusEnum != null) {
            return feedbackRepository.findByStatus(statusEnum, pageable);
        } else if (filter != null && !filter.isEmpty()) {
            return feedbackRepository.findByContentContainingIgnoreCaseOrMentee_FullNameContainingIgnoreCase(
                    filter, filter, pageable
            );
        } else {
            return feedbackRepository.findAll(pageable);
        }
    }
    @Override
    public void deleteFeedbackById(UUID id) {
        feedbackRepository.deleteById(id);
    }

    @Override
    public void toggleFeedbackStatus(UUID id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));
        if (feedback.getStatus() == Feedback.Status.ACTIVE) {
            feedback.setStatus(Feedback.Status.HIDDEN);
        } else {
            feedback.setStatus(Feedback.Status.ACTIVE);
        }
        feedbackRepository.save(feedback);
    }

    @Override
    public void aiVerifyFeedback(Feedback feedback) {
        String prompt = generatePrompt(feedback);
        String aiResponse = llmService.callModel(prompt);
        processAIResponse(feedback, aiResponse);
    }

    @Override
    public String generatePrompt(Feedback feedback) {
        return "Hello";
    }

    @Override
    public void processAIResponse(Feedback feedback, String aiResponse) {

    }


}
