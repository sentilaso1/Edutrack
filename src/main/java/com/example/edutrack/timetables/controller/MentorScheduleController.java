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
                                 @RequestParam String monthOption,
                                 HttpSession session) {

        if (day == null || slot == null || day.isEmpty() || day.size() != slot.size()) {
            return "redirect:/mentor/working-date?error=invalid-day-slot";
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        if ("current".equals(monthOption)) {
            startDate = today.withDayOfMonth(1);
            endDate = today.withDayOfMonth(today.lengthOfMonth());

        } else if ("next".equals(monthOption)) {
            LocalDate firstDayNextMonth = today.plusMonths(1).withDayOfMonth(1);
            LocalDate lastDayThisMonth = today.withDayOfMonth(today.lengthOfMonth());

            // Tính tuần cuối cùng là 7 ngày cuối của tháng
            LocalDate startOfLastWeekThisMonth = lastDayThisMonth.minusDays(6);

            // Kiểm tra nếu hôm nay không thuộc tuần cuối cùng → từ chối đăng ký
            if (today.isBefore(startOfLastWeekThisMonth)) {
                return "redirect:/mentor/working-date?error=too-early-for-next-month";
            }

            startDate = firstDayNextMonth;
            endDate = firstDayNextMonth.withDayOfMonth(firstDayNextMonth.lengthOfMonth());

        } else {
            return "redirect:/mentor/working-date?error=invalid-month-selection";
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
            availableTimes.add(new MentorAvailableTime(mentor, slot.get(i), day.get(i), startDate, endDate));
        }

        mentorAvailableTimeService.insertWorkingSchedule(availableTimes);
        return "redirect:/mentor/working-date";
    }


}
