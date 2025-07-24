package com.example.edutrack.profiles.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.*;

public class CVForm {
    @NotBlank
    private String summary;

    @NotNull
    private Integer experienceYears;

    @NotBlank
    private String skills;

    @NotBlank
    private String education;

    private String experience;
    private String certifications;
    private String languages;
    private String portfolioUrl;

    private String selectedCourses;
    private List<CourseApplicationDetail> courseDetails = new ArrayList<>();

    public List<CourseApplicationDetail> getCourseDetails() {
        return courseDetails;
    }

    public void setCourseDetails(List<CourseApplicationDetail> courseDetails) {
        this.courseDetails = courseDetails;
    }

    @NotNull
    private UUID userId;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCertifications() {
        return certifications;
    }

    public void setCertifications(String certifications) {
        this.certifications = certifications;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getSelectedCourses() {
        return selectedCourses;
    }

    public void setSelectedCourses(String selectedCourses) {
        this.selectedCourses = selectedCourses;
    }
}
