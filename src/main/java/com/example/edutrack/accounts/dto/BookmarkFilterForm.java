package com.example.edutrack.accounts.dto;

import jakarta.validation.constraints.Null;

import java.io.Serializable;
import java.util.List;

public class BookmarkFilterForm implements Serializable {
    public static final String SORT_DATE_DESC = "date-desc";
    public static final String SORT_DATE_ASC = "date-asc";

    @Null
    private List<Integer> tags;

    @Null
    private String sort;

    public BookmarkFilterForm() {
    }

    public BookmarkFilterForm(List<Integer> tags, String sort) {
        this.tags = tags;
        this.sort = sort;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "BookmarkFilterForm{" +
                "tags=" + tags +
                ", sort='" + sort + '\'' +
                '}';
    }
}
