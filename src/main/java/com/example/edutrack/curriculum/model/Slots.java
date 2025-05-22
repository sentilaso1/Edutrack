package com.example.edutrack.curriculum.model;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "SLOTS")
public class Slots {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalTime startTime;
    private LocalTime endTime;

    public LocalTime getStartTime() {
        return startTime;
    }
    public LocalTime getEndTime() {
        return endTime;
    }
}