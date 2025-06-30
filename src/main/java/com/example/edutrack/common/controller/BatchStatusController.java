package com.example.edutrack.common.controller;

import com.example.edutrack.curriculum.model.FeedbackReport;
import com.example.edutrack.curriculum.service.interfaces.FeedbackReportService;
import com.example.edutrack.curriculum.service.interfaces.FeedbackService;
import com.example.edutrack.profiles.service.interfaces.CvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class BatchStatusController {
    private final CvService cvService;
    private final FeedbackReportService feedbackReportService;

    public BatchStatusController(CvService cvService, FeedbackReportService feedbackReportService) {
        this.cvService = cvService;
        this.feedbackReportService = feedbackReportService;
    }

    @GetMapping("batch/status")
    public Map<String, Object> getBatchStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("running", cvService.isBatchRunning());
        status.put("lastEnd", cvService.getLastBatchEnd());
        status.put("reportRunning", feedbackReportService.isReportBatchRunning());
        status.put("reportLastEnd", feedbackReportService.getLastReportBatchEnd());
        return status;
    }




}