package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.common.service.interfaces.LLMService;
import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.model.FeedbackReport;
import com.example.edutrack.curriculum.repository.FeedbackReportRepository;
import com.example.edutrack.curriculum.repository.FeedbackRepository;
import com.example.edutrack.curriculum.service.interfaces.FeedbackReportService;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.service.CvServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FeedbackReportServiceImpl implements FeedbackReportService {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackReportServiceImpl.class);

    private final FeedbackReportRepository feedbackReportRepository;
    private final LLMService llmService;
    private final FeedbackRepository feedbackRepository;

    public FeedbackReportServiceImpl(FeedbackReportRepository feedbackReportRepository,
                                     LLMService llmService,
                                     FeedbackRepository feedbackRepository) {
        this.feedbackReportRepository = feedbackReportRepository;
        this.llmService = llmService;
        this.feedbackRepository = feedbackRepository;
    }


    @Override
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

    @Override
    @Transactional
    public void setStatus(UUID id, FeedbackReport.Status status) {
        FeedbackReport report = feedbackReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("FeedbackReport not found"));
        report.setStatus(status);
        feedbackReportRepository.save(report);
    }

    @Override
    public String generateReportPrompt(Feedback feedback, FeedbackReport report) {
        String feedbackContent = feedback.getContent() != null ? feedback.getContent().replace("\"", "\\\"") : "";
        String reporter = report.getReporter() != null ? report.getReporter().getFullName().replace("\"", "\\\"") : "unknown";
        String category = report.getCategory() != null ? report.getCategory().name() : "";
        String reason = report.getReason() != null ? report.getReason().replace("\"", "\\\"") : "";

        return """
    You are an AI moderator on an education platform. Analyze the following feedback and report, then decide if the feedback violates community guidelines and should be hidden, or is safe and should remain visible.

    Review logic:
    - Hide feedback if it contains spam, offensive, harassing, misinformation, or other inappropriate content.
    - Keep feedback visible if it is polite, constructive, or doesn't violate community standards.
    - Pay attention to the report category and reporter's reason, but make your own judgment based on content.
    - Empty/vague reports should not cause hiding unless the feedback clearly violates rules.

    You MUST reply ONLY with valid JSON (no markdown, no code block, no natural language, no explanation, no commentary, no  tags, no backticks). Return ONLY the following structure:
    {
      "should-hide": "true" or "false",
      "reason": "Short justification for the action."
    }
    
    Do NOT add any extra text, explanation, or formatting. Do NOT wrap in backticks or code blocks.

    Example valid responses:
    {
      "should-hide": "true",
      "reason": "Feedback contains offensive language."
    }
    {
      "should-hide": "false",
      "reason": "Feedback is constructive and does not violate guidelines."
    }

    Data to review:
    {
      "feedback": "%s",
      "reporter": "%s",
      "report-category": "%s",
      "report-reason": "%s"
    }
    """.formatted(feedbackContent, reporter, category, reason);
    }

    @Override
    public void aiVerifyFeedbackReport(Feedback feedback, FeedbackReport report) {
        String prompt = generateReportPrompt(feedback, report);
        String aiResponse = llmService.callModel(prompt);
        processAIResponse(feedback, aiResponse);
    }

    @Override
    public void processAIResponse(Feedback feedback, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            if (aiResponse == null || aiResponse.trim().isEmpty()) return;

            JsonNode root = mapper.readTree(aiResponse);
            if (root.has("choices")) {
                JsonNode message = root.path("choices").get(0).path("message");
                if (!message.isMissingNode() && message.has("content")) {
                    aiResponse = message.get("content").asText();
                }
            }
            if (aiResponse.startsWith("```")) {
                aiResponse = aiResponse.replaceAll("^```(json)?", "").replaceAll("```$", "").trim();
            }

            JsonNode decision = mapper.readTree(aiResponse);
            boolean shouldHide = decision.get("should-hide").asText().equalsIgnoreCase("true");
            String reason = decision.get("reason").asText();

            List<FeedbackReport> reports = feedbackReportRepository.findByFeedbackAndStatus(feedback, FeedbackReport.Status.PENDING);

            if (shouldHide) {
                feedback.setStatus(Feedback.Status.HIDDEN);
                feedbackRepository.save(feedback);
                for (FeedbackReport r : reports) {
                    r.setStatus(FeedbackReport.Status.HIDDEN);
                }
            } else {
                for (FeedbackReport r : reports) {
                    r.setStatus(FeedbackReport.Status.AIREVIEWED);
                }
            }
            feedbackReportRepository.saveAll(reports);

            logger.info("AI moderation: feedbackId={} hide={} reason={}", feedback.getId(), shouldHide, reason);

        } catch (Exception e) {
            logger.error("Error processing AI feedback moderation: {}", aiResponse, e);
        }
    }

    private volatile boolean reportBatchRunning = false;
    private volatile LocalDateTime lastReportBatchStart;
    private volatile LocalDateTime lastReportBatchEnd;

    @Override
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void scheduleAIFeedbackModeration() {
        reportBatchRunning = true;
        lastReportBatchStart = LocalDateTime.now();
        try {
            List<FeedbackReport> pendingReports = feedbackReportRepository.findByStatus(FeedbackReport.Status.PENDING);
            logger.info("Pending Report size: {} entities", pendingReports.size());
            for (FeedbackReport report : pendingReports) {
                Feedback feedback = report.getFeedback();
                if (feedback.getStatus() == Feedback.Status.ACTIVE) {
                    aiVerifyFeedbackReport(feedback, report);
                }
            }
        } finally {
            lastReportBatchEnd = LocalDateTime.now();
            reportBatchRunning = false;
        }
    }

    public boolean isReportBatchRunning() { return reportBatchRunning; }
    public LocalDateTime getLastReportBatchEnd() { return lastReportBatchEnd; }
}
