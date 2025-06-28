package com.example.edutrack.curriculum.controller;

import com.example.edutrack.curriculum.dto.FeedbackReportFilterForm;
import com.example.edutrack.curriculum.model.FeedbackReport;
import com.example.edutrack.curriculum.repository.FeedbackReportRepository;
import com.example.edutrack.curriculum.service.interfaces.FeedbackReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
public class FeedbackReportController {
    public static final int PAGE_SIZE = 15;

    private final FeedbackReportRepository feedbackReportRepository;
    private final FeedbackReportService feedbackReportService;

    public FeedbackReportController(FeedbackReportRepository feedbackReportRepository,
                                    FeedbackReportService feedbackReportService) {
        this.feedbackReportRepository = feedbackReportRepository;
        this.feedbackReportService = feedbackReportService;
    }

    @GetMapping("/admin/reports/list/{page}")
    public String listFeedbackReports(
            @ModelAttribute FeedbackReportFilterForm params,
            Model model,
            @PathVariable int page) {

        if (page - 1 < 0) {
            return "redirect:/404";
        }

        String filter = params.getFilter();    // e.g. search reporter, reason
        String sort = params.getSort();        // e.g. date, status
        String status = params.getStatus();    // e.g. "PENDING", "REVIEWED"
        String category = params.getCategory();// e.g. "SPAM", "OFFENSIVE", etc.

        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);

        Page<FeedbackReport> reportPage = feedbackReportService.queryFeedbackReports(filter, sort, status, category, pageable);

        model.addAttribute("pageNumber", page);
        model.addAttribute("page", reportPage);

        if (sort != null) model.addAttribute("sort", sort);
        if (filter != null) model.addAttribute("filter", filter);
        if (status != null) model.addAttribute("status", status);
        if (category != null) model.addAttribute("category", category);

        if (reportPage.getTotalPages() > 0 && page > reportPage.getTotalPages()) {
            return "redirect:/404";
        }

        return "manager/list-report";
    }

    @GetMapping("/admin/reports/list")
    public String redirectToListFeedbackReports() {
        return "redirect:/admin/reports/list/1";
    }

    @GetMapping("/admin/reports/delete/{id}")
    public String deleteFeedbackReport(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            feedbackReportRepository.deleteReportById(id);
            redirectAttributes.addFlashAttribute("success", "Report deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete report: " + e.getMessage());
        }
        return "redirect:/admin/reports/list/1";
    }

}
