package com.example.edutrack.timetables.service.interfaces;

import com.example.edutrack.timetables.model.MentorAvailableTime;
import com.example.edutrack.timetables.model.MentorAvailableTimeId;
import com.example.edutrack.timetables.model.Schedule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ScheduleService {
    public void insertWorkingSchedule(List<MentorAvailableTime> schedule);
}
