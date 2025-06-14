package com.example.edutrack.accounts.controller;


import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller(value="mentor")
public class MentorController {
    private final EnrollmentScheduleService enrollmentScheduleService;
    private final EnrollmentService enrollmentService;

    public MentorController(EnrollmentScheduleService enrollmentScheduleService, EnrollmentService enrollmentService) {
        this.enrollmentScheduleService = enrollmentScheduleService;
        this.enrollmentService = enrollmentService;
    }

    @RequestMapping("/mentor")
    public String mentor(Model model,
                         HttpSession session) {

        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        return "mentor-dashboard";
    }

    @GetMapping("mentor/schedule")
    public String viewWeek(@RequestParam(value = "weekStart", required = false) LocalDate weekStart,
                           HttpSession session,
                           Model model) {

        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }

        if (weekStart == null) {
            weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        }
        LocalDate weekEnd = weekStart.plusDays(6);

        List<EnrollmentSchedule> schedules = enrollmentScheduleService.findByMentorAndDateBetween(mentor, weekStart, weekEnd);

        for (EnrollmentSchedule schedule : schedules) {
            System.out.println("Course: " + schedule.getEnrollment().getCourseMentor().getCourse().getName());
            System.out.println("Mentee: " + schedule.getEnrollment().getMentee().getFullName());
            System.out.println("Date: " + schedule.getDate());
            System.out.println("Slot: " + schedule.getSlot().name());
        }

        model.addAttribute("schedules", schedules);
        model.addAttribute("startDate", weekStart);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        model.addAttribute("startDateFormatted", weekStart.format(dateFormatter));
        model.addAttribute("endDateFormatted", weekEnd.format(dateFormatter));

        List<LocalDate> daysOfWeek = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            daysOfWeek.add(weekStart.plusDays(i));
        }

        DateTimeFormatter dayLabelFormatter = DateTimeFormatter.ofPattern("EEE dd/MM", Locale.ENGLISH);
        List<String> dayLabels = daysOfWeek.stream()
                .map(d -> d.format(dayLabelFormatter))
                .toList();

        model.addAttribute("dayLabels", dayLabels);
        model.addAttribute("previousWeek", weekStart.minusWeeks(1));
        model.addAttribute("nextWeek", weekStart.plusWeeks(1));
        model.addAttribute("slots", Slot.values());

        return "mentor_calendar";
    }

    @GetMapping("/mentor/sensor-class")
    public String viewSensorClassList(Model model,
                                      @RequestParam(defaultValue = "PENDING") Enrollment.EnrollmentStatus status,
                                      HttpSession session){
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        List<Enrollment> enrollmentList = enrollmentService.findByStatusAndMentor(status, mentor.getId());
        model.addAttribute("enrollmentList", enrollmentList);
        return "skill-register-request";
    }
    @GetMapping("/mentor/schedule/{esid}")
    public String menteeReview(Model model, @PathVariable Integer esid, HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        EnrollmentSchedule enrollmentSchedule = enrollmentScheduleService.findById(esid);
        if(enrollmentSchedule == null) {
            return "redirect:/mentor/schedule?error=enrollmentNotFound";
        }
        if(!enrollmentSchedule.getEnrollment().getCourseMentor().getMentor().getId().equals(mentor.getId())) {
            return "redirect:/mentor/schedule?error=notMentor";
        }
        model.addAttribute("enrollmentSchedule", enrollmentSchedule);
        return "mentee-review";
    }

    @GetMapping("/mentor/sensor-class/{eid}")
    public String rejectRegistration(@PathVariable Long eid,
                                     @RequestParam String action,
                                     HttpSession session){
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        Enrollment enrollment = enrollmentService.findById(eid);
        if(enrollment == null) {
            return "redirect:/mentor/schedule?error=enrollmentNotFound";
        }
        if(!enrollment.getCourseMentor().getMentor().getId().equals(mentor.getId())) {
            return "redirect:/mentor/schedule?error=notMentor";
        }
        if(action.equals("reject")) {
            enrollment.setStatus(Enrollment.EnrollmentStatus.REJECTED);
            enrollmentService.save(enrollment);
            return "redirect:/mentor/sensor-class?reject=" + eid;
        }
        return "redirect:/mentor/sensor-class";
    }

}
