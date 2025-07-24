package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.model.FeedbackReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedbackReportRepository extends JpaRepository<FeedbackReport, UUID> {

    List<FeedbackReport> findByFeedbackAndStatus(Feedback feedback, FeedbackReport.Status status);
    Page<FeedbackReport> findByStatus(FeedbackReport.Status status, Pageable pageable);
    List<FeedbackReport> findByStatus(FeedbackReport.Status status);
    Page<FeedbackReport> findByCategory(FeedbackReport.Category category, Pageable pageable);
    Page<FeedbackReport> findByStatusAndCategory(FeedbackReport.Status status, FeedbackReport.Category category, Pageable pageable);
    Page<FeedbackReport> findByReasonContainingIgnoreCaseOrReporter_FullNameContainingIgnoreCase(
            String reason, String reporter, Pageable pageable);
    Page<FeedbackReport> findByStatusAndCategoryAndReasonContainingIgnoreCaseOrReporter_FullNameContainingIgnoreCase(
            FeedbackReport.Status status, FeedbackReport.Category category, String reason, String reporter, Pageable pageable);
}
