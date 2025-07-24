package com.example.edutrack.curriculum.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.curriculum.dto.FeedbackReportFilterForm;
import com.example.edutrack.curriculum.model.Feedback;
import com.example.edutrack.curriculum.model.FeedbackReport;
import com.example.edutrack.curriculum.repository.FeedbackReportRepository;
import com.example.edutrack.curriculum.repository.FeedbackRepository;
import com.example.edutrack.curriculum.service.interfaces.FeedbackReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Controller
public class FeedbackReportController {
    public static final int PAGE_SIZE = 15;

    private final FeedbackReportRepository feedbackReportRepository;
    private final FeedbackReportService feedbackReportService;
    private final FeedbackRepository feedbackRepository;

    public FeedbackReportController(FeedbackReportRepository feedbackReportRepository,
                                    FeedbackReportService feedbackReportService,
                                    FeedbackRepository feedbackRepository) {
        this.feedbackReportRepository = feedbackReportRepository;
        this.feedbackReportService = feedbackReportService;
        this.feedbackRepository = feedbackRepository;
    }

    @GetMapping("/manager/reports/list/{page}")
    public String listFeedbackReports(
            @ModelAttribute FeedbackReportFilterForm params,
            Model model,
            @PathVariable int page) {

        if (page - 1 < 0) {
            return "redirect:/404";
        }

        String filter = params.getFilter();
        String sort = params.getSort();
        String status = params.getStatus();
        String category = params.getCategory();

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);

        Page<FeedbackReport> reportPage = feedbackReportService.queryFeedbackReports(filter, sort, status, category, pageable);

        boolean reportBatchRunning = feedbackReportService.isReportBatchRunning();
        LocalDateTime lastReportBatchEnd = feedbackReportService.getLastReportBatchEnd();
        long delaySeconds = 60;
        long nextReportBatchMillis = reportBatchRunning
                ? -1
                : (lastReportBatchEnd != null
                ? lastReportBatchEnd.plusSeconds(delaySeconds).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                : System.currentTimeMillis() + delaySeconds * 1000);

        model.addAttribute("pageNumber", page);
        model.addAttribute("page", reportPage);
        model.addAttribute("nextReportBatchMillis", nextReportBatchMillis);
        model.addAttribute("reportBatchRunning", reportBatchRunning);

        if (sort != null) model.addAttribute("sort", sort);
        if (filter != null) model.addAttribute("filter", filter);
        if (status != null) model.addAttribute("status", status);
        if (category != null) model.addAttribute("category", category);

        if (reportPage.getTotalPages() > 0 && page > reportPage.getTotalPages()) {
            return "redirect:/404";
        }

        return "manager/list-report";
    }

    @GetMapping("/manager/reports/list")
    public String redirectToListFeedbackReports() {
        return "redirect:/manager/reports/list/1";
    }

    @GetMapping("/manager/reports/delete/{id}")
    public String deleteFeedbackReport(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            feedbackReportRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Report deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete report: " + e.getMessage());
        }
        return "redirect:/manager/reports/list/1";
    }

    @PostMapping("/review/report")
    @ResponseBody
    public String submitFeedbackReport(@RequestParam("feedbackId") UUID feedbackId,
                                       @RequestParam("category") String categoryStr,
                                       @RequestParam("reason") String reason,
                                       HttpSession session,
                                       RedirectAttributes ra){
        try {
            Mentee mentee = (Mentee) session.getAttribute("loggedInUser");
            Feedback feedback = feedbackRepository.findById(feedbackId)
                    .orElseThrow(() -> new IllegalArgumentException("Feedback not found"));

            FeedbackReport.Category category = FeedbackReport.Category.valueOf(categoryStr);

            FeedbackReport report = new FeedbackReport(feedback, mentee, category, reason);
            feedbackReportRepository.save(report);

            ra.addFlashAttribute("success", "Report submit successfully.");
            return "success";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to submit report: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/manager/reports/review-action/{reportId}")
    public String handleReviewAction(
            @PathVariable UUID reportId,
            @RequestParam String action,
            RedirectAttributes ra) {
        try {
            FeedbackReport targetReport = feedbackReportRepository.findById(reportId)
                    .orElseThrow(() -> new IllegalArgumentException("Report not found"));
            Feedback feedback = targetReport.getFeedback();


            if (feedback.getStatus() == Feedback.Status.HIDDEN || feedback.getStatus() == Feedback.Status.ACTIVE) {

                if ("hide".equalsIgnoreCase(action)) {
                    feedback.setStatus(Feedback.Status.HIDDEN);
                    targetReport.setStatus(FeedbackReport.Status.HIDDEN);
                    ra.addFlashAttribute("success", "Feedback hidden & all reports reviewed!");
                } else if ("active".equalsIgnoreCase(action)) {
                    feedback.setStatus(Feedback.Status.ACTIVE);
                    targetReport.setStatus(FeedbackReport.Status.REVIEWED);
                    ra.addFlashAttribute("success", "Feedback set as active & all reports reviewed!");
                } else {
                    ra.addFlashAttribute("error", "Invalid action!");
                    return "redirect:/manager/reports/list/1";
                }
                feedbackRepository.save(feedback);
                feedbackReportRepository.save(targetReport);

                List<FeedbackReport> reports = feedbackReportRepository.findByFeedbackAndStatus(feedback, FeedbackReport.Status.PENDING);
                for (FeedbackReport r : reports) {
                    r.setStatus(FeedbackReport.Status.REVIEWED);
                }
                feedbackReportRepository.saveAll(reports);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Could not update: " + e.getMessage());
        }
        return "redirect:/manager/reports/list/1";
    }


}
