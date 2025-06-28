package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.model.FeedbackReport;
import com.example.edutrack.curriculum.repository.FeedbackReportRepository;
import com.example.edutrack.curriculum.service.interfaces.FeedbackReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FeedbackReportServiceImpl implements FeedbackReportService {
    private final FeedbackReportRepository feedbackReportRepository;

    public FeedbackReportServiceImpl(FeedbackReportRepository feedbackReportRepository) {
        this.feedbackReportRepository = feedbackReportRepository;
    }


    public Page<FeedbackReport> queryFeedbackReports(String filter, String sort, String status, String category, Pageable pageable) {
        FeedbackReport.Status statusEnum = null;
        FeedbackReport.Category categoryEnum = null;

        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = FeedbackReport.Status.valueOf(status);
            } catch (IllegalArgumentException ex) {
                statusEnum = null;
            }
        }
        if (category != null && !category.isEmpty()) {
            try {
                categoryEnum = FeedbackReport.Category.valueOf(category);
            } catch (IllegalArgumentException ex) {
                categoryEnum = null;
            }
        }

        if (statusEnum != null && categoryEnum != null && filter != null && !filter.isEmpty()) {
            return feedbackReportRepository
                    .findByStatusAndCategoryAndReasonContainingIgnoreCaseOrReporter_FullNameContainingIgnoreCase(
                            statusEnum, categoryEnum, filter, filter, pageable);
        } else if (statusEnum != null && categoryEnum != null) {
            return feedbackReportRepository.findByStatusAndCategory(statusEnum, categoryEnum, pageable);
        } else if (statusEnum != null) {
            return feedbackReportRepository.findByStatus(statusEnum, pageable);
        } else if (categoryEnum != null) {
            return feedbackReportRepository.findByCategory(categoryEnum, pageable);
        } else if (filter != null && !filter.isEmpty()) {
            return feedbackReportRepository.findByReasonContainingIgnoreCaseOrReporter_FullNameContainingIgnoreCase(
                    filter, filter, pageable);
        } else {
            return feedbackReportRepository.findAll(pageable);
        }
    }

}
