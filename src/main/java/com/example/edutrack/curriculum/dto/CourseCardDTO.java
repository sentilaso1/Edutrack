package com.example.edutrack.curriculum.dto;


import com.example.edutrack.accounts.model.Mentor;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;

import java.util.UUID;

public class CourseCardDTO {
    private UUID courseMentorId;
    private String courseTitle;
    private int studentCount;
    private Mentor mentor;

    // Constructors
    public CourseCardDTO() {}



    public CourseCardDTO(UUID id, String name,int studentCount, Mentor mentor) {
        this.courseMentorId = id;
        this.courseTitle = name;
        this.studentCount = studentCount;
        this.mentor = mentor;
    }

    // Getters and setters
    public UUID getCourseMentorId() {
        return courseMentorId;
    }

    public void setCourseMentorId(UUID courseMentorId) {
        this.courseMentorId = courseMentorId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    public Mentor getMentor() {
        return mentor;
    }
    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }
}
