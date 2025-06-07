package com.example.edutrack.curriculum.dto;

import com.example.edutrack.curriculum.model.Tag;

public class TagEnrollmentCountDTO {
    private Tag tag;
    private Long enrollmentCount;

    public TagEnrollmentCountDTO(Tag tag, Long enrollmentCount) {
        this.tag = tag;
        this.enrollmentCount = enrollmentCount;
    }

    public Tag getTag() {
        return tag;
    }
    public void setTag(Tag tag) {
        this.tag = tag;
    }
    public Long getEnrollmentCount() {
        return enrollmentCount;
    }
    public void setEnrollmentCount(Long enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }
}
