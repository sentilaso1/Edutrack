package com.example.edutrack.curriculum.dto;

import com.example.edutrack.timetables.model.EnrollmentSchedule;

import java.time.LocalDate;
import java.util.UUID;

public class ScheduleDTO {
    private Long id;
    private String slot;
    private String day;
    private String courseName;
    private String mentorName;
    private String startTime;
    private String endTime;
    private UUID mentorId;

    private int startHour;
    private int startMinute;
    private int durationInMinutes;
    private int endHour;
    private boolean available;

    private boolean hasTest;
    private boolean canReschedule;
    private EnrollmentSchedule.RescheduleStatus rescheduleStatus;
    private LocalDate date;

    public ScheduleDTO() {}

    public ScheduleDTO(Long id, String slot, String day, String courseName, String mentorName,
                       String startTime, String endTime,
                       int startHour, int startMinute, int durationInMinutes,
                       boolean hasTest, boolean canReschedule,
                       EnrollmentSchedule.RescheduleStatus rescheduleStatus, LocalDate date, UUID mentorId, boolean available) {
        this.id = id;
        this.slot = slot;
        this.day = day;
        this.courseName = courseName;
        this.mentorName = mentorName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.durationInMinutes = durationInMinutes;
        this.endHour = startHour + (int) Math.ceil(durationInMinutes / 60.0);
        this.hasTest = hasTest;
        this.canReschedule = canReschedule;
        this.rescheduleStatus = rescheduleStatus;
        this.date = date;
        this.mentorId = mentorId;
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public UUID getMentorId() {
        return mentorId;
    }

    public void setMentorId(UUID mentorId) {
        this.mentorId = mentorId;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getMentorName() { return mentorName; }
    public void setMentorName(String mentorName) { this.mentorName = mentorName; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getStartHour() { return startHour; }
    public void setStartHour(int startHour) { this.startHour = startHour; }

    public int getStartMinute() { return startMinute; }
    public void setStartMinute(int startMinute) { this.startMinute = startMinute; }

    public int getDurationInMinutes() { return durationInMinutes; }
    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
        this.endHour = this.startHour + (int) Math.ceil(durationInMinutes / 60.0);
    }

    public int getEndHour() { return endHour; }
    public void setEndHour(int endHour) { this.endHour = endHour; }

    public boolean isHasTest() { return hasTest; }
    public void setHasTest(boolean hasTest) { this.hasTest = hasTest; }

    public boolean isCanReschedule() { return canReschedule; }
    public void setCanReschedule(boolean canReschedule) { this.canReschedule = canReschedule; }

    public EnrollmentSchedule.RescheduleStatus getRescheduleStatus() {
        return rescheduleStatus;
    }

    public void setRescheduleStatus(EnrollmentSchedule.RescheduleStatus rescheduleStatus) {
        this.rescheduleStatus = rescheduleStatus;
    }
}
