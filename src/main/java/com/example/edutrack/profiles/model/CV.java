package com.example.edutrack.profiles.model;

import com.example.edutrack.accounts.model.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "cv")
public class CV {
    public static final String ITEM_SEPARATOR_REGEX = "[,;]+";

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_APPROVED = "approved";
    public static final String STATUS_REJECTED = "rejected";

    @Id
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "summary", length = 256, nullable = false)
    private String summary;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears = 0;

    @Column(name = "skills", length = 512, nullable = false)
    private String skills;

    @Column(name = "education", length = 512, nullable = false)
    private String education;

    @Column(name = "experience", length = 512)
    private String experience;

    @Column(name = "certifications", length = 512)
    private String certifications;

    @Column(name = "languages", length = 512)
    private String languages;

    @Column(name = "portfolio_url", length = 1024)
    private String portfolioUrl;

    @Column(name = "status", nullable = false)
    private String status = STATUS_PENDING;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private Date createdDate = new Date();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public CV() {

    }

    public CV(String summary, Integer experienceYears, String skills, String education, User user) {
        this.summary = summary;
        this.experienceYears = experienceYears;
        this.skills = skills;
        this.education = education;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        if (summary == null || summary.isEmpty()) {
            throw new IllegalArgumentException("Summary must not be empty");
        }
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
        if (skills == null || skills.isEmpty()) {
            throw new IllegalArgumentException("Skills must not be empty");
        }
        this.skills = skills;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        if (education == null || education.isEmpty()) {
            throw new IllegalArgumentException("Education must not be empty");
        }
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status must not be empty");
        }

        if (!status.equals(STATUS_PENDING) && !status.equals(STATUS_APPROVED) && !status.equals(STATUS_REJECTED)) {
            throw new IllegalArgumentException("Status must be pending, approved or rejected");
        }
        this.status = status;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date updatedDate) {
        this.createdDate = updatedDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private static List<String> getItemList(String itemString) {
        if (itemString == null || itemString.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(itemString.split(ITEM_SEPARATOR_REGEX))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public List<String> getSkillItems() {
        return getItemList(getSkills());
    }

    public List<String> getEducationItems() {
        return getItemList(getEducation());
    }

    public List<String> getExperienceItems() {
        return getItemList(getExperience());
    }

    public List<String> getCertificationItems() {
        return getItemList(getCertifications());
    }

    public List<String> getLanguageItems() {
        return getItemList(getLanguages());
    }

    @Override
    public String toString() {
        return "CV{" +
                "id=" + id +
                ", summary='" + summary + '\'' +
                ", experienceYears=" + experienceYears +
                ", skills='" + skills + '\'' +
                ", education='" + education + '\'' +
                ", experience='" + experience + '\'' +
                ", certifications='" + certifications + '\'' +
                ", languages='" + languages + '\'' +
                ", portfolioUrl='" + portfolioUrl + '\'' +
                ", status='" + status + '\'' +
                ", updatedDate=" + createdDate +
                ", user=" + user +
                '}';
    }
}
