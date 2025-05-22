package com.example.edutrack.profiles.model;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "cv")
public class CV {
    @Id
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "summary", length = 256, nullable = false)
    private String summary;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears;

    @Column(name = "skills", length = 512, nullable = false)
    private String skills;

    @Column(name = "education", length = 512, nullable = false)
    private String education;

    @Column(name = "certifications", length = 512, nullable = false)
    private String certifications;

    @Column(name = "languages", length = 512, nullable = false)
    private String languages;

    @Column(name = "portfolio_url", length = 1024, nullable = false)
    private String portfolioUrl;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = Boolean.FALSE;

    @Column(name = "updated_date", nullable = false)
    @LastModifiedDate
    private Date updatedDate = new Date();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public CV() {

    }

    public CV(String summary, Integer experienceYears, String skills, String education, String certifications, String languages, String portfolioUrl, User user) {
        this.summary = summary;
        this.experienceYears = experienceYears;
        this.skills = skills;
        this.education = education;
        this.certifications = certifications;
        this.languages = languages;
        this.portfolioUrl = portfolioUrl;
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

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "CV{" +
                "id=" + id +
                ", summary='" + summary + '\'' +
                ", experienceYears=" + experienceYears +
                ", skills='" + skills + '\'' +
                ", education='" + education + '\'' +
                ", certifications='" + certifications + '\'' +
                ", languages='" + languages + '\'' +
                ", portfolioUrl='" + portfolioUrl + '\'' +
                ", updatedDate=" + updatedDate +
                ", user=" + user +
                '}';
    }
}
