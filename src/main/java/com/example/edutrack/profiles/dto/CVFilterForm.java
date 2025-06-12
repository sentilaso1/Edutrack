package com.example.edutrack.profiles.dto;

import jakarta.validation.constraints.Null;

import java.io.Serializable;
import java.util.List;

public class CVFilterForm implements Serializable {
    public static final String FILTER_APPROVED = "approved";
    public static final String FILTER_REJECTED = "rejected";
    public static final String FILTER_PENDING = "pending";
    public static final String FILTER_AIAPPROVED = "aiapproved";

    public static final String SORT_DATE_ASC = "date-asc";
    public static final String SORT_DATE_DESC = "date-desc";

    @Null
    private String sort;
    @Null
    private String filter;
    @Null
    private List<String> tags;

    public CVFilterForm(String sort, String filter, List<String> tags) {
        this.sort = sort;
        this.filter = filter;
        this.tags = tags;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        if (sort == null || sort.isEmpty()) {
            this.sort = sort;
            return;
        }

        if (!sort.equals(SORT_DATE_ASC) && !sort.equals(SORT_DATE_DESC)) {
            throw new IllegalArgumentException("Invalid sort: " + sort);
        }
        this.sort = sort;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        if (filter == null || filter.isEmpty()) {
            this.filter = filter;
            return;
        }

        if (!filter.equals(FILTER_APPROVED) && !filter.equals(FILTER_PENDING) && !filter.equals(FILTER_REJECTED) && !filter.equals(FILTER_AIAPPROVED)) {
            throw new IllegalArgumentException("Invalid filter: " + filter);
        }
        this.filter = filter;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "CVFilterForm{" +
                "sort='" + sort + '\'' +
                ", filter='" + filter + '\'' +
                ", tags=" + tags +
                '}';
    }
}
