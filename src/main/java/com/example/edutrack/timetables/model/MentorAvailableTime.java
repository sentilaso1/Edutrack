package com.example.edutrack.timetables.model;

import com.example.edutrack.accounts.model.Mentor;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "mentor_available_time")
public class MentorAvailableTime {
    @EmbeddedId
    private MentorAvailableTimeId id;

    @ManyToOne
    @MapsId("mentorId")
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    public MentorAvailableTime() {
    }

    public MentorAvailableTime(Mentor mentor, Slot slot, Day day, Date startDate, Date endDate) {
        this.id = new MentorAvailableTimeId(mentor.getId(), slot, day, startDate, endDate);
        this.mentor = mentor;
    }

    public MentorAvailableTimeId getId() {
        return id;
    }

    public void setId(MentorAvailableTimeId id) {
        this.id = id;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    @Override
    public String toString() {
        return "MentorAvailableTime{" +
                "id=" + id +
                ", mentor=" + mentor +
                '}';
    }
}
