package com.example.edutrack.timetables.service.interfaces;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.MentorAvailableTime;
import com.example.edutrack.timetables.model.Slot;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MentorAvailableTimeService {
    public void insertWorkingSchedule(List<MentorAvailableTime> schedule);
}
