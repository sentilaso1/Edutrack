package com.example.edutrack.timetables.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.MentorAvailableTime;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.service.interfaces.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MentorScheduleController {
    private final ScheduleService scheduleService;

    public MentorScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/add-new-schedule")
    public String addNewSchedule(@RequestParam List<Day> day, @RequestParam List<Slot> slot, HttpSession session, HttpServletRequest request) {
        if(day == null || slot == null || day.isEmpty() ||day.size() != slot.size()) {
            return "redirect:/mentor?error=invalid-day-slot";
        }
        Mentor mentor = session.getAttribute("loggedInUser") == null ? new Mentor() : (Mentor) session.getAttribute("loggedInUser");
        List<MentorAvailableTime> availableTimes = new ArrayList<>();
        for(int i = 0; i < day.size(); i++) {
            availableTimes.add(new MentorAvailableTime(mentor, slot.get(i), day.get(i)));
        }
        scheduleService.insertWorkingSchedule(availableTimes);
        return "mentor-dashboard";
    }
}
