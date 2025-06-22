package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.dto.ReviewDTO;
import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.repository.FeedbackRepository;
import com.example.edutrack.curriculum.service.interfaces.FeedbackService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackRepository feedbackRepository;
    public FeedbackServiceImpl(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
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
}
