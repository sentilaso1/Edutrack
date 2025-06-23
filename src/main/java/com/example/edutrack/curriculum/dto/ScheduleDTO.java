package com.example.edutrack.curriculum.dto;

public class ScheduleDTO {
    private String slot;
    private String day;
    private String courseName;
    private String mentorName;
    private String startTime;
    private String endTime;

    private int startHour;
    private int startMinute;
    private int durationInMinutes;
    private boolean hasTest;

    private int endHour;

    public ScheduleDTO() {}

    public ScheduleDTO(String slot, String day, String courseName, String mentorName,
                       String startTime, String endTime,
                       int startHour, int startMinute, int durationInMinutes, boolean hasTest) {
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
    }

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

    public boolean isHasTest() {
        return hasTest;
    }

    public void setHasTest(boolean hasTest) {
        this.hasTest = hasTest;
    }
}
