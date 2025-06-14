package com.example.edutrack.timetables.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.MentorAvailableTime;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                                 @RequestParam String start,
                                 @RequestParam String end,
                                 HttpSession session) {
        if (day == null || slot == null || day.isEmpty() || day.size() != slot.size() || start == null || start.isEmpty() || end == null || end.isEmpty()) {
            return "redirect:/mentor/working-date?error=invalid-day-slot";
        }


        Mentor mentor = session.getAttribute("loggedInUser") == null ? new Mentor() : (Mentor) session.getAttribute("loggedInUser");
        List<MentorAvailableTime> availableTimes = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startLocal = LocalDate.parse(start, formatter);
        LocalDate endLocal = LocalDate.parse(end, formatter);

        String error = mentorAvailableTimeService.alertValidStartEndTime(startLocal, endLocal, mentor);
        if (error != null) {
            return "redirect:/mentor/working-date?error=" + error;
        }

        for (int i = 0; i < day.size(); i++) {
            availableTimes.add(new MentorAvailableTime(mentor, slot.get(i), day.get(i), startLocal, endLocal));
        }

        mentorAvailableTimeService.insertWorkingSchedule(availableTimes);
        return "redirect:/mentor/working-date";
    }

}
