package com.example.edutrack.timetables.model;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.Course;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "schedules")
public class Schedule {
    public enum ScheduleAttendance {
        NOT_YET, PRESENT, ABSENT, CANCELLED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentee_id", nullable = false)
    private Mentee mentee;

    @ManyToOne
    @JoinColumn(name = "mentor_id", nullable = false)
    private Mentor mentor;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance", nullable = false)
    private ScheduleAttendance attendance;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot", nullable = false)
    private Slot slot;

    @Column(name = "date", nullable = false)
    private Date date;

    public Schedule() {
    }

    public Schedule(Mentee mentee, Mentor mentor, Course course, ScheduleAttendance attendance, Slot slot, Date date) {
        this.mentee = mentee;
        this.mentor = mentor;
        this.course = course;
        this.attendance = attendance;
        this.slot = slot;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mentee getMentee() {
        return mentee;
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }

    public Mentor getMentor() {
        return mentor;
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public ScheduleAttendance getAttendance() {
        return attendance;
    }

    public void setAttendance(ScheduleAttendance attendance) {
        this.attendance = attendance;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", mentee=" + mentee +
                ", mentor=" + mentor +
                ", course=" + course +
                ", attendance=" + attendance +
                ", slot=" + slot +
                ", date=" + date +
                '}';
    }
}
