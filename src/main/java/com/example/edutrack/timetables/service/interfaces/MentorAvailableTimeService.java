package com.example.edutrack.timetables.service.interfaces;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.dto.MentorAvailableSlotDTO;
import com.example.edutrack.timetables.dto.MentorAvailableTimeDTO;
import com.example.edutrack.timetables.model.MentorAvailableTime;
import com.example.edutrack.timetables.model.MentorAvailableTimeDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public interface MentorAvailableTimeService {
    void insertWorkingSchedule(List<MentorAvailableTime> schedule);

    List<MentorAvailableTime> findByMentorId(Mentor mentor);

    String alertValidStartEndTime(LocalDate start, LocalDate end, Mentor mentor);

    List<MentorAvailableTimeDTO> findAllDistinctStartEndDates(Mentor mentor, MentorAvailableTime.Status status);

    List<MentorAvailableSlotDTO> findAllSlotByEndDate(Mentor mentor, LocalDate endDate);

    LocalDate findMaxEndDate(Mentor mentor);

    LocalDate findMinStartDate(Mentor mentor);

    List<MentorAvailableTimeDTO> findAllDistinctStartEndDates(MentorAvailableTime.Status status);

    boolean[][] slotDayMatrix(List<MentorAvailableSlotDTO> setSlots);

    List<MentorAvailableTime> findAllMentorAvailableTimeByEndDate(Mentor mentor, LocalDate endDate);

    LocalDate findMaxEndDateByStatus(Mentor mentor, MentorAvailableTime.Status enumValue);

    void insertMentorAvailableTime(LocalDate startDate, LocalDate endDate, Mentor foundMentor);

    List<MentorAvailableTimeDetails> findByMentor(Mentor mentor);

    List<MentorAvailableTimeDetails> findByMentorIdAndStatusAndDateRange(
            UUID mentorId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<MentorAvailableSlotDTO> findOnlyApprovedSlotsByEndDate(Mentor mentor, LocalDate endDate);
}
