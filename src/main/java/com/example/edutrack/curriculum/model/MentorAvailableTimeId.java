package com.example.edutrack.curriculum.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.UUID;

public class MentorAvailableTimeId implements Serializable {
    private UUID mentorId;
    private Integer slotId;
    private DayOfWeek day;
}