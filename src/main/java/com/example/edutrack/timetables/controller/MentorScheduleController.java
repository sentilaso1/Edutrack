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
                                 @RequestParam(required = false) String monthOption,
                                 @RequestParam(required = false) LocalDate startDate,
                                 @RequestParam(required = false) LocalDate endDate,
                                 @RequestParam String btn,
                                 HttpSession session) {

        if (day == null || slot == null || day.isEmpty() || day.size() != slot.size()) {
            return "redirect:/mentor/working-date?error=invalid-day-slot";
        }
        if(startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            if ("current".equals(monthOption)) {
                startDate = today.withDayOfMonth(1);
                endDate = today.withDayOfMonth(today.lengthOfMonth());

            } else if ("next".equals(monthOption)) {
                LocalDate firstDayNextMonth = today.plusMonths(1).withDayOfMonth(1);
                LocalDate lastDayThisMonth = today.withDayOfMonth(today.lengthOfMonth());

                LocalDate startOfLastWeekThisMonth = lastDayThisMonth.minusDays(6);

                if (today.isBefore(startOfLastWeekThisMonth)) {
                    return "redirect:/mentor/working-date?error=too-early-for-next-month";
                }

                startDate = firstDayNextMonth;
                endDate = firstDayNextMonth.withDayOfMonth(firstDayNextMonth.lengthOfMonth());

            } else {
                return "redirect:/mentor/working-date?error=invalid-month-selection";
            }
        }

        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }

        String error = mentorAvailableTimeService.alertValidStartEndTime(startDate, endDate, mentor);
        if (error != null) {
            return "redirect:/mentor/working-date?error=" + error;
        }

        List<MentorAvailableTime> availableTimes = new ArrayList<>();
        for (int i = 0; i < day.size(); i++) {
            MentorAvailableTime newMentorAvailableTime = new MentorAvailableTime(mentor, slot.get(i), day.get(i), startDate, endDate);
            if(btn.equals("draft")){
                newMentorAvailableTime.setStatus(MentorAvailableTime.Status.DRAFT);
            } else if (btn.equals("submit")) {
                newMentorAvailableTime.setStatus(MentorAvailableTime.Status.PENDING);
            }
            availableTimes.add(newMentorAvailableTime);
        }

        mentorAvailableTimeService.insertWorkingSchedule(availableTimes);
        return "redirect:/mentor/working-date";
    }


}
