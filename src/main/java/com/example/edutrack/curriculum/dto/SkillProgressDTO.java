package com.example.edutrack.curriculum.dto;

import java.time.LocalDate;
import java.util.List;

public class SkillProgressDTO {
    private String courseTitle;
    private List<String> tags;
    private int progressPercentage;
    private int sessionsCompleted;
    private int totalSessions;
    private LocalDate lastSessionDate;

    public SkillProgressDTO(String courseTitle, LocalDate lastSessionDate, int sessionsCompleted, int progressPercentage, List<String> tags, int totalSessions) {
        this.courseTitle = courseTitle;
        this.lastSessionDate = lastSessionDate;
        this.sessionsCompleted = sessionsCompleted;
        this.progressPercentage = progressPercentage;
        this.tags = tags;
        this.totalSessions = totalSessions;
    }

    public int getTotalSessions() {
        return totalSessions;
    }
    public void setTotalSessions(int totalSessions) {
        this.totalSessions = totalSessions;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public int getSessionsCompleted() {
        return sessionsCompleted;
    }

    public void setSessionsCompleted(int sessionsCompleted) {
        this.sessionsCompleted = sessionsCompleted;
    }

    public LocalDate getLastSessionDate() {
        return lastSessionDate;
    }

    public void setLastSessionDate(LocalDate lastSessionDate) {
        this.lastSessionDate = lastSessionDate;
    }
}
