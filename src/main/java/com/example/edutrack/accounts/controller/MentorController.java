package com.example.edutrack.accounts.controller;


import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.timetables.dto.MentorAvailableSlotDTO;
import com.example.edutrack.timetables.dto.MentorAvailableTimeDTO;
import com.example.edutrack.timetables.dto.RequestedSchedule;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.service.implementation.EnrollmentScheduleServiceImpl;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
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

@Controller(value = "mentor")
public class MentorController {
    private final EnrollmentScheduleService enrollmentScheduleService;
    private final EnrollmentService enrollmentService;
    private final MentorAvailableTimeService mentorAvailableTimeService;

    public MentorController(EnrollmentScheduleService enrollmentScheduleService,
                            EnrollmentService enrollmentService,
                            MentorAvailableTimeService mentorAvailableTimeService) {
        this.enrollmentScheduleService = enrollmentScheduleService;
        this.enrollmentService = enrollmentService;
        this.mentorAvailableTimeService = mentorAvailableTimeService;
    }

    @RequestMapping("/mentor")
    public String mentor(Model model,
                         HttpSession session) {

        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        return "/mentor/mentor-dashboard";
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

        return "/mentor/mentor_calendar";
    }

    @GetMapping("/mentor/sensor-class")
    public String viewSensorClassList(Model model,
                                      @RequestParam(defaultValue = "PENDING") Enrollment.EnrollmentStatus status,
                                      HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        List<Enrollment> enrollmentList = enrollmentService.findByStatusAndMentor(status, mentor.getId());
        model.addAttribute("status", status);
        model.addAttribute("enrollmentList", enrollmentList);
        return "mentor/skill-register-request";
    }

    @GetMapping("/mentor/schedule/{esid}")
    public String menteeReview(Model model, @PathVariable Integer esid, HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        EnrollmentSchedule enrollmentSchedule = enrollmentScheduleService.findById(esid);
        if (enrollmentSchedule == null) {
            return "redirect:/mentor/schedule?error=enrollmentNotFound";
        }
        if (!enrollmentSchedule.getEnrollment().getCourseMentor().getMentor().getId().equals(mentor.getId())) {
            return "redirect:/mentor/schedule?error=notMentor";
        }
        model.addAttribute("enrollmentSchedule", enrollmentSchedule);
        return "mentor/mentee-review";
    }

    @GetMapping("/mentor/sensor-class/{eid}")
    public String rejectRegistration(@PathVariable Long eid,
                                     @RequestParam String action,
                                     HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        Enrollment enrollment = enrollmentService.findById(eid);
        if (enrollment == null) {
            return "redirect:/mentor/schedule?error=enrollmentNotFound";
        }
        if (!enrollment.getCourseMentor().getMentor().getId().equals(mentor.getId())) {
            return "redirect:/mentor/schedule?error=notMentor";
        }
        if (action.equals("reject")) {
            enrollment.setStatus(Enrollment.EnrollmentStatus.REJECTED);
            enrollmentService.save(enrollment);
            return "redirect:/mentor/sensor-class?status=REJECTED&reject=" + eid;
        }
        if(action.equals("approve")){
            enrollment.setStatus(Enrollment.EnrollmentStatus.APPROVED);
            enrollmentScheduleService.saveEnrollmentSchedule(enrollment);
            return "redirect:/mentor/sensor-class?status=APPROVED&approve=" + eid;
        }if(action.equals("view")){
            return "redirect:/mentor/sensor-class/" + eid + "/view";
        }
        return "redirect:/mentor/sensor-class";
    }

    @GetMapping("/mentor/sensor-class/{eid}/view")
    public String viewSensorClass(@PathVariable Long eid,
                                  Model model,
                                  HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        Enrollment enrollment = enrollmentService.findById(eid);
        List<Slot> slots = new ArrayList<>();
        List<Day> days = new ArrayList<>();
        String summary = enrollment.getScheduleSummary();

        EnrollmentScheduleServiceImpl.parseDaySlotString(summary, days, slots);

        List<RequestedSchedule> startTime = enrollmentScheduleService.findStartLearningTime(enrollment.getMentee(), enrollment.getCourseMentor(), slots, days, enrollment.getTotalSlots());
        model.addAttribute("startTime", startTime);
        return "mentor/skill-register-request-detail";
    }

    @GetMapping("/mentor/working-date")
    public String workingDate(Model model,
                              @RequestParam(required = false) String end,
            HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }

        List<MentorAvailableTimeDTO> setTime = mentorAvailableTimeService.findAllDistinctStartEndDates(mentor);
        model.addAttribute("setTime", setTime);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endLocal;
        if (end == null || end.isEmpty()) {
            endLocal = mentorAvailableTimeService.findMaxEndDate(mentor);
        } else {
            endLocal = LocalDate.parse(end, formatter);
        }
        List<MentorAvailableSlotDTO> setSlots = mentorAvailableTimeService.findAllSlotByEndDate(mentor, endLocal);
        boolean[][] slotDayMatrix = new boolean[Slot.values().length][Day.values().length];

        for (MentorAvailableSlotDTO dto : setSlots) {
            int slotIndex = dto.getSlot().ordinal();
            int dayIndex = dto.getDay().ordinal();
            slotDayMatrix[slotIndex][dayIndex] = true;
        }

        model.addAttribute("slotDayMatrix", slotDayMatrix);

        System.out.println("SIZE: " + setSlots.size());

//        List<Slot> slots = Arrays.stream(Slot.values()).toList();
//        List<Day> days = Arrays.stream(Day.values()).toList();
        model.addAttribute("slots", Slot.values());
        model.addAttribute("days", Day.values());

        return "/mentor/mentor-working-date";
    }

    @GetMapping("/mentor/stats")
    public String mentorStats(Model model, HttpSession session) {
        return "accounts/html/mentor-stats";
    }

    @GetMapping("mentor/classes")
    public String mentorClasses(Model model, HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }

        List<Enrollment> ongoingEnrollments = enrollmentService.findOngoingEnrollments(mentor.getId());
        model.addAttribute("ongoingEnrollments", ongoingEnrollments);
        return "mentor/mentor-class";
    }

    @GetMapping("mentor/classes/{eid}")
    public String detailClass(@PathVariable Long eid, Model model, HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        Enrollment enrollment = enrollmentService.findById(eid);
        List<EnrollmentSchedule> enrollmentSchedule = enrollmentScheduleService.findEnrollmentScheduleByEnrollment(enrollment);
        model.addAttribute("enrollmentSchedules", enrollmentSchedule);
        return "mentor/mentor-detail-class";

    }
}
