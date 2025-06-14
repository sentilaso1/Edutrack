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

    @PostMapping("/add-new-schedule")
    public String addNewSchedule(@RequestParam List<Day> day,
                                 @RequestParam List<Slot> slot,

                                 HttpSession session) {
        if(day == null || slot == null || day.isEmpty() ||day.size() != slot.size()) {
            return "redirect:/mentor?error=invalid-day-slot";
        }
        Mentor mentor = session.getAttribute("loggedInUser") == null ? new Mentor() : (Mentor) session.getAttribute("loggedInUser");
        List<MentorAvailableTime> availableTimes = new ArrayList<>();
        for(int i = 0; i < day.size(); i++) {
            availableTimes.add(new MentorAvailableTime(mentor, slot.get(i), day.get(i)));
        }
        mentorAvailableTimeService.insertWorkingSchedule(availableTimes);
        return "redirect:/mentor/working-date";
    }
}
