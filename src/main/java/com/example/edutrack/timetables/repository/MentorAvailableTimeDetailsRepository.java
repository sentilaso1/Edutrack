package com.example.edutrack.timetables.repository;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.model.MentorAvailableTimeDetails;
import com.example.edutrack.timetables.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MentorAvailableTimeDetailsRepository extends JpaRepository<MentorAvailableTimeDetails, Long> {

    List<MentorAvailableTimeDetails> findByMentor(Mentor mentor);

    boolean existsByMentorAndSlotAndDateAndMenteeIsNull(Mentor mentor, Slot slot, LocalDate date);

    boolean existsBySlotAndDateAndMentee(Slot slot, LocalDate date, Mentee mentee);



}
