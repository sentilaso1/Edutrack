package com.example.edutrack.curriculum.service.interfaces;


import com.example.edutrack.curriculum.model.FeedbackReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedbackReportService {

    Page<FeedbackReport> queryFeedbackReports(String filter, String sort, String status, String category, Pageable pageable);

}
