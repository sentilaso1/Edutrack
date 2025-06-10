package com.example.edutrack.timetables.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "enrollment_schedule")
public class EnrollmentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot", nullable = false)
    private Slot slot;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "is_test")
    private Boolean isTest = Boolean.FALSE;

    @Column(name = "score")
    private Integer score;

    public EnrollmentSchedule() {
    }


}
