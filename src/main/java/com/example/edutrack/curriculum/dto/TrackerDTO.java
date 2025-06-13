package com.example.edutrack.curriculum.dto;

public class TrackerDTO {
    private int skillProcess;
    private int totalTrackedSkills;
    private String goalCompleted;
    private int totalSkillsCompleted;

    public TrackerDTO(int skillProcess, String goalCompleted, int totalTrackedSkills, int totalSkillsCompleted) {
        this.skillProcess = skillProcess;
        this.goalCompleted = goalCompleted;
        this.totalTrackedSkills = totalTrackedSkills;
        this.totalSkillsCompleted = totalSkillsCompleted;
    }

    public int getSkillProcess() {
        return skillProcess;
    }

    public void setSkillProcess(int skillProcess) {
        this.skillProcess = skillProcess;
    }

    public int getTotalTrackedSkills() {
        return totalTrackedSkills;
    }

    public void setTotalTrackedSkills(int totalTrackedSkills) {
        this.totalTrackedSkills = totalTrackedSkills;
    }

    public String getGoalCompleted() {
        return goalCompleted;
    }

    public void setGoalCompleted(String goalCompleted) {
        this.goalCompleted = goalCompleted;
    }

    public int getTotalSkillsCompleted() {
        return totalSkillsCompleted;
    }

    public void setTotalSkillsCompleted(int totalSkillsCompleted) {
        this.totalSkillsCompleted = totalSkillsCompleted;
    }




}
