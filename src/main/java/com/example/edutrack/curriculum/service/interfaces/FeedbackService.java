package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FeedbackService {
    Page<ReviewDTO> getFilteredReviewsByMentee(UUID menteeId, String keyword, Integer rating, Pageable pageable);
}
