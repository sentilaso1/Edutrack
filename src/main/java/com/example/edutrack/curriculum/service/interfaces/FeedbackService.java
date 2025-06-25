package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.curriculum.dto.ReviewDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface FeedbackService {
    Page<ReviewDTO> getFilteredReviewsByMentee(UUID menteeId, String keyword, Integer rating, Pageable pageable);

    Feedback submitFeedback(String content, Double rating, Mentee mentee, CourseMentor courseMentor);

    List<Feedback> getAllFeedbacksByMentorId(UUID mentorId);

    Page<Feedback> queryFeedbacks(String filter, String sort, String status, Pageable pageable);

    void deleteFeedbackById(UUID id);

    void toggleFeedbackStatus(UUID id);
}
