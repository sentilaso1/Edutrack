package com.example.edutrack.curriculum.model;

import jakarta.persistence.*;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;

import java.time.DayOfWeek;
import java.util.UUID;

@Entity
@Table(name = "MENTOR_AVAILABLE_TIME")
@IdClass(MentorAvailableTimeId.class)
public class MentorAvailableTime {
    @Id
    private UUID mentorId;

    @Id
    private Integer slotId;

    @Id
    @Enumerated(EnumType.STRING)
    private DayOfWeek day;

    @ManyToOne
    @JoinColumn(name = "slotId", insertable = false, updatable = false)
    private Slots slot;


    public UUID getMentorId() {
        return mentorId;
    }
    public void setMentorId(UUID mentorId) {
        this.mentorId = mentorId;
    }

    public Slots getSlot() {
        return slot;
    }


    public DayOfWeek getDay() {
        return day;
    }

}
