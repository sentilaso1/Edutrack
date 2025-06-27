package com.example.edutrack.timetables.model;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "mentor_available_time_details")
public class MentorAvailableTimeDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @ManyToOne
    @JoinColumn(name = "mentee_id")
    private Mentee mentee;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot", nullable = false)
    private Slot slot;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    public MentorAvailableTimeDetails() {
    }

    public MentorAvailableTimeDetails(Mentor mentor, Slot slot, LocalDate date) {
        this.mentor = mentor;
        this.slot = slot;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MentorAvailableTimeDetails{" +
               "id=" + id +
               ", mentor=" + mentor +
               ", mentee=" + mentee +
               ", slot=" + slot +
               ", date=" + date +
               '}';
    }
}
