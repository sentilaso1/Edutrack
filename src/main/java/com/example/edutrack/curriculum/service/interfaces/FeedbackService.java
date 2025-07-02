package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.curriculum.dto.ReviewDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.profiles.model.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FeedbackService {
    Page<ReviewDTO> getFilteredReviewsByMentee(UUID menteeId, String keyword, Integer rating, Pageable pageable);

    Feedback submitFeedback(String content, Double rating, boolean isAnonymous, Mentee mentee, CourseMentor courseMentor);

    List<Feedback> getAllFeedbacksByMentorId(UUID mentorId);


}
