package com.example.edutrack.timetables.service.implementation;

import com.example.edutrack.timetables.model.MentorAvailableTime;
import com.example.edutrack.timetables.repository.MentorAvailableTimeRepository;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import org.springframework.stereotype.Service;

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
}
