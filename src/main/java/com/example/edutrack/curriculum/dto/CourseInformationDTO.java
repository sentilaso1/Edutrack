package com.example.edutrack.curriculum.dto;

import com.example.edutrack.common.model.CustomFormatter;
import com.example.edutrack.curriculum.model.Course;

public record CourseInformationDTO(Course course, double price, double rating, int mentors) {
    @Override
    public Course course() {
        return course;
    }

    @Override
    public double price() {
        return price;
    }

    public String priceFormatted() {
        return CustomFormatter.formatNumberWithSpaces(price);
    }

    @Override
    public double rating() {
        return rating;
    }

    @Override
    public int mentors() {
        return mentors;
    }
}
