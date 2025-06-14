package com.example.edutrack.timetables.service.implementation;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.dto.MentorAvailableSlotDTO;
import com.example.edutrack.timetables.dto.MentorAvailableTimeDTO;
import com.example.edutrack.timetables.model.MentorAvailableTime;
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
    public String alertValidStartEndTime(LocalDate start,  LocalDate end, Mentor mentor) {
        if(start.isAfter(end)) {
            return "Start time cannot be after end time";
        }
        if(end.isBefore(LocalDate.now())) {
            return "End time cannot be before now";
        }
        LocalDate lastestEndDate = mentorAvailableTimeRepository.findMaxEndDate(mentor);
        if(lastestEndDate != null && start.isBefore(lastestEndDate)) {
            return "Start time must be after" + lastestEndDate;
        }
        return null;
    }

    @Override
    public List<MentorAvailableTimeDTO> findAllDistinctStartEndDates(Mentor mentor) {
        return mentorAvailableTimeRepository.findAllDistinctStartEndDates(mentor);
    }

    @Override
    public List<MentorAvailableSlotDTO> findAllSlotByEndDate(Mentor mentor, LocalDate endDate) {
        return mentorAvailableTimeRepository.findAllSlotByEndDate(mentor, endDate);
    }

    @Override
    public LocalDate findMaxEndDate(Mentor mentor) {
        return mentorAvailableTimeRepository.findMaxEndDate(mentor);
    }

}
