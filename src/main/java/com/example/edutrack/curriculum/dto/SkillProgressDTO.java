package com.example.edutrack.curriculum.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class SkillProgressDTO {
    public UUID courseId;
    public UUID courseMentorId;
    private String courseTitle;
    private List<String> tags;
    private int progressPercentage;
    private int sessionsCompleted;
    private int totalSessions;
    private LocalDate lastSessionDate;
    private String mentorName;
    private UUID mentorId;
    private LocalDate startDate;

    public SkillProgressDTO(UUID courseId, UUID courseMentorId,String courseTitle, LocalDate lastSessionDate, int sessionsCompleted, int progressPercentage,
                            List<String> tags, int totalSessions,
                            String mentorName, UUID mentorId) {
        this.courseTitle = courseTitle;
        this.courseMentorId = courseMentorId;
        this.lastSessionDate = lastSessionDate;
        this.sessionsCompleted = sessionsCompleted;
        this.progressPercentage = progressPercentage;
        this.tags = tags;
        this.totalSessions = totalSessions;
        this.mentorName = mentorName;
        this.mentorId = mentorId;
        this.startDate = startDate;
        this.courseId = courseId;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
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

    public String getMentorName() {
        return mentorName;
    }
    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public UUID getMentorId() {
        return mentorId;
    }
    public void setMentorId(UUID mentorId) {
        this.mentorId = mentorId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }


    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public List<String> getTags() {
        return tags;
    }

    public UUID getCourseMentorId() {
        return courseMentorId;
    }

    public void setCourseMentorId(UUID courseMentorId) {
        this.courseMentorId = courseMentorId;
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
