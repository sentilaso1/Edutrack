package com.example.edutrack.timetables.repository;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.model.MentorAvailableTimeDetails;
import com.example.edutrack.timetables.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MentorAvailableTimeDetailsRepository extends JpaRepository<MentorAvailableTimeDetails, Long> {

    List<MentorAvailableTimeDetails> findByMentor(Mentor mentor);

    boolean existsByMentorAndSlotAndDateAndMenteeIsNull(Mentor mentor, Slot slot, LocalDate date);

    boolean existsBySlotAndDateAndMentee(Slot slot, LocalDate date, Mentee mentee);

    List<MentorAvailableTimeDetails> findByMentorIdAndMenteeIsNullAndDateBetween(UUID mentorId, LocalDate startDate, LocalDate endDate);

    MentorAvailableTimeDetails findByMentorAndDateAndSlot(Mentor mentor, LocalDate date, Slot slot);
    MentorAvailableTimeDetails findByMentorAndDateAndSlotAndMenteeIsNull(Mentor mentor, LocalDate date, Slot slot);
}