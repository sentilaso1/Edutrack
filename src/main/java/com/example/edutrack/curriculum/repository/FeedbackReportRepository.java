package com.example.edutrack.curriculum.repository;

import com.example.edutrack.curriculum.model.FeedbackReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedbackReportRepository extends JpaRepository<FeedbackReport, UUID> {
    void deleteReportById(UUID id);

    Page<FeedbackReport> findByStatus(FeedbackReport.Status status, Pageable pageable);
    Page<FeedbackReport> findByCategory(FeedbackReport.Category category, Pageable pageable);
    Page<FeedbackReport> findByStatusAndCategory(FeedbackReport.Status status, FeedbackReport.Category category, Pageable pageable);
    Page<FeedbackReport> findByReasonContainingIgnoreCaseOrReporter_FullNameContainingIgnoreCase(
            String reason, String reporter, Pageable pageable);
    Page<FeedbackReport> findByStatusAndCategoryAndReasonContainingIgnoreCaseOrReporter_FullNameContainingIgnoreCase(
            FeedbackReport.Status status, FeedbackReport.Category category, String reason, String reporter, Pageable pageable);
}
