package com.example.edutrack.timetables.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.MentorAvailableTime;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MentorScheduleController {
    private final MentorAvailableTimeService mentorAvailableTimeService;

    public MentorScheduleController(MentorAvailableTimeService mentorAvailableTimeService) {
        this.mentorAvailableTimeService = mentorAvailableTimeService;
    }
}
