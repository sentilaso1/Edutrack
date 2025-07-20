package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.common.service.interfaces.LLMService;
import com.example.edutrack.curriculum.dto.ReviewDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.FeedbackRepository;
import com.example.edutrack.curriculum.service.interfaces.FeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackServiceImpl.class);

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
    public Feedback submitFeedback(String content, Double rating, boolean isAnonymous, Mentee mentee, CourseMentor courseMentor) {
        if (feedbackRepository.findByMenteeAndCourseMentor(mentee, courseMentor).isPresent()) {
            throw new IllegalStateException("Feedback already submitted for this course and mentor.");
        }
        Feedback feedback = new Feedback(content, rating, isAnonymous, mentee, courseMentor);
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
    public long countReviewsByMentor(Mentor mentor) {
        return feedbackRepository.countByCourseMentor_Mentor(mentor);
    }

    @Override
    public List<Feedback> getTopRecentFeedback(CourseMentor courseMentor) {
        return feedbackRepository.findTop3ByCourseMentorOrderByCreatedDateDesc(courseMentor);
    }
}
