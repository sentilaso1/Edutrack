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

    public Slots() {}

    public Slots(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }
    public LocalTime getEndTime() {
        return endTime;
    }
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String toString() {
        return "Slots {id=" + id + ", startTime=" + startTime + ", endTime=" + endTime + "}";
    }
}