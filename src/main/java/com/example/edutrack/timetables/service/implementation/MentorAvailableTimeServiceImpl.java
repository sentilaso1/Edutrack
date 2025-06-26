package com.example.edutrack.timetables.service.implementation;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.dto.MentorAvailableSlotDTO;
import com.example.edutrack.timetables.dto.MentorAvailableTimeDTO;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.MentorAvailableTime;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.repository.MentorAvailableTimeRepository;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MentorAvailableTimeServiceImpl implements MentorAvailableTimeService {

    MentorAvailableTimeRepository mentorAvailableTimeRepository;

    public MentorAvailableTimeServiceImpl(MentorAvailableTimeRepository scheduleRepository) {
        this.mentorAvailableTimeRepository = scheduleRepository;
    }

    @Override
    public void insertWorkingSchedule(List<MentorAvailableTime> schedule) {
        mentorAvailableTimeRepository.saveAll(schedule);
    }

    @Override
    public List<MentorAvailableTime> findByMentorId(Mentor mentor) {
        return mentorAvailableTimeRepository.findByMentorId(mentor.getId());
    }

    @Override
    public String alertValidStartEndTime(LocalDate start, LocalDate end, Mentor mentor) {
        List<MentorAvailableTime> a = mentorAvailableTimeRepository.findMentorAvailableTimeByStatus(end,mentor, MentorAvailableTime.Status.DRAFT, MentorAvailableTime.Status.REJECTED);
        for(MentorAvailableTime b : a) {
            System.out.print(b.getId().getEndDate() + " ");
            System.out.print(b.getId().getStartDate() + " ");
            System.out.println(b.getStatus());
        }

        if(start.isAfter(end)) {
            return "Start time cannot be after end time";
        }
        if(end.isBefore(LocalDate.now())) {
            return "End time cannot be before now";
        }
        if(mentorAvailableTimeRepository.isEndDateExisted(end,mentor, MentorAvailableTime.Status.DRAFT, MentorAvailableTime.Status.REJECTED)) {
            return "Time Range has been registered";
        }
        return null;
    }

    @Override
    public List<MentorAvailableTimeDTO> findAllDistinctStartEndDates(Mentor mentor, MentorAvailableTime.Status status) {
        return mentorAvailableTimeRepository.findAllDistinctStartEndDates(mentor, status);
    }

    @Override
    public List<MentorAvailableSlotDTO> findAllSlotByEndDate(Mentor mentor, LocalDate endDate) {
        return mentorAvailableTimeRepository.findAllSlotByEndDate(mentor, endDate);
    }

    @Override
    public LocalDate findMaxEndDate(Mentor mentor) {
        return mentorAvailableTimeRepository.findMaxEndDate(mentor);
    }

    @Override
    public LocalDate findMinStartDate(Mentor mentor) {
        return mentorAvailableTimeRepository.findMinStartDate(mentor);
    }

    @Override
    public List<MentorAvailableTimeDTO> findAllDistinctStartEndDates(MentorAvailableTime.Status status) {
        return mentorAvailableTimeRepository.findAllDistinctStartEndDates(status);
    }

    @Override
    public boolean[][] slotDayMatrix(List<MentorAvailableSlotDTO> setSlots){
        boolean[][] slotDayMatrix = new boolean[Slot.values().length][Day.values().length];

        for (MentorAvailableSlotDTO dto : setSlots) {
            int slotIndex = dto.getSlot().ordinal();
            int dayIndex = dto.getDay().ordinal();
            slotDayMatrix[slotIndex][dayIndex] = true;
        }
        return slotDayMatrix;
    }

    @Override
    public List<MentorAvailableTime> findAllMentorAvailableTimeByEndDate(Mentor mentor, LocalDate endDate) {
        return mentorAvailableTimeRepository.findAllMentorAvailableTimeByEndDate(mentor, endDate);
    }

    @Override
    public LocalDate findMaxEndDateByStatus(Mentor mentor, MentorAvailableTime.Status enumValue) {
        return mentorAvailableTimeRepository.findMaxEndDateByStatus(mentor, enumValue);
    }


}
