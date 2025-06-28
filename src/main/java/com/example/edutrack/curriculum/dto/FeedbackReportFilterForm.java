package com.example.edutrack.curriculum.dto;

public class FeedbackReportFilterForm {
    private String filter;   // free text: search reporter, reason, etc.
    private String sort;     // "createdDate", "status", etc.
    private String status;   // PENDING, REVIEWED, DISMISSED
    private String category; // SPAM, OFFENSIVE, etc.

    public String getFilter() {
        return filter;
    }
    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSort() {
        return sort;
    }
    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}