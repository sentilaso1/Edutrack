package com.example.edutrack.curriculum.dto;

public class FeedbackFilterForm {
    private String filter; // free text (e.g., search by mentee or content)
    private String sort;   // e.g., "createdDate", "rating", "status"
    private String status; // filter by ACTIVE/HIDDEN

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
}