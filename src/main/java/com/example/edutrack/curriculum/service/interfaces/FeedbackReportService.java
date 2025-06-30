package com.example.edutrack.curriculum.service.interfaces;


import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.model.FeedbackReport;
import com.example.edutrack.profiles.model.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface FeedbackReportService {

    Page<FeedbackReport> queryFeedbackReports(String filter, String sort, String status, String category, Pageable pageable);
    void setStatus(UUID id, FeedbackReport.Status status);

    String generateReportPrompt(Feedback feedback, FeedbackReport report);
    void aiVerifyFeedbackReport(Feedback feedback, FeedbackReport report);
    void processAIResponse(Feedback feedback, String aiJson);
    void scheduleAIFeedbackModeration();
    boolean isReportBatchRunning();
    LocalDateTime getLastReportBatchEnd();
}
