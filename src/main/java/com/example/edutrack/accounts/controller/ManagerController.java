package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.interfaces.MenteeService;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import com.example.edutrack.timetables.dto.MentorAvailableTimeDTO;
import com.example.edutrack.timetables.dto.RequestedSchedule;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.service.implementation.EnrollmentScheduleServiceImpl;
import com.example.edutrack.timetables.service.implementation.EnrollmentServiceImpl;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import com.example.edutrack.accounts.service.interfaces.ManagerStatsService;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.edutrack.accounts.dto.RevenueChartDTO;
import com.example.edutrack.accounts.dto.TopMentorDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.example.edutrack.accounts.dto.ManagerStatsDTO;

@Controller
public class ManagerController {
    private final MentorService mentorService;
    private final MenteeService menteeService;
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final EnrollmentService enrollmentService;
    private final EnrollmentScheduleService enrollmentScheduleService;
    private final ManagerStatsService managerStatsService;

    @Autowired
    public ManagerController(MentorService mentorService,
                             MenteeService menteeService,
                             MentorAvailableTimeService mentorAvailableTimeService,
                             EnrollmentService enrollmentServiceImpl,
                             EnrollmentScheduleService enrollmentScheduleService, 
                             ManagerStatsService managerStatsService) {
        this.mentorService = mentorService;
        this.menteeService = menteeService;
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.enrollmentService = enrollmentServiceImpl;
        this.enrollmentScheduleService = enrollmentScheduleService;
        this.managerStatsService = managerStatsService;
    }

    @GetMapping("/manager/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("mentorCount", mentorService.countAll());
        model.addAttribute("menteeCount", menteeService.countAll());

        return "manager/dashboard";
    }

    @GetMapping("/manager/mentees")
    public String showMentees(Model model) {
        model.addAttribute("mentees", menteeService.findAll());
        return "manager/mentees";
    }

    @GetMapping("/manager/mentors")
    public String showMentors(Model model) {
        model.addAttribute("mentors", mentorService.findAll());
        return "manager/mentors";
    }

    @GetMapping("/manager/stats")
    public String showStats(Model model, @RequestParam(defaultValue = "week") String period) {
        ManagerStatsDTO stats = managerStatsService.getManagerStats(period);
        model.addAttribute("stats", stats);
        model.addAttribute("currentPeriod", period);
        return "accounts/html/manager-stats";
    }

    @GetMapping("/api/revenue-chart")
    @ResponseBody
    public ResponseEntity<List<RevenueChartDTO>> getRevenueChart(
            @RequestParam(defaultValue = "week") String period) {
        LocalDateTime startDate = getStartDateByPeriod(period);
        List<RevenueChartDTO> chartData = managerStatsService.getRevenueChartData(period, startDate);
        return ResponseEntity.ok(chartData);
    }
    
    @GetMapping("/api/top-mentors")
    @ResponseBody
    public ResponseEntity<List<TopMentorDTO>> getTopMentors(
            @RequestParam(defaultValue = "week") String period) {
        LocalDateTime startDate = getStartDateByPeriod(period);
        List<TopMentorDTO> topMentors = managerStatsService.getTopMentors(startDate);
        return ResponseEntity.ok(topMentors);
    }
    
    @GetMapping("/api/summary-stats")
    @ResponseBody
    public ResponseEntity<ManagerStatsDTO> getSummaryStats(
            @RequestParam(defaultValue = "week") String period) {
        ManagerStatsDTO stats = managerStatsService.getManagerStats(period);
        return ResponseEntity.ok(stats);
    }
    
    private LocalDateTime getStartDateByPeriod(String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period) {
            case "week":
                return now.minusDays(7);
            case "month":
                return now.minusDays(30);
            case "quarter":
                return now.minusDays(90);
            case "year":
                return now.minusDays(365);
            default:
                return now.minusDays(7);
        }
    }

    @GetMapping("/manager/mentor-working-date")
    public String showMentorWorkingDate(Model model) {
        List<MentorAvailableTimeDTO> mentorRequest = mentorAvailableTimeService.findAllDistinctStartEndDates();
        model.addAttribute("mentorRequest", mentorRequest);
        return "manager/working-date-review";
    }

    @GetMapping("/manager/create-class")
    public String showCreateClass(Model model) {
        List<Enrollment> enrollmentRequests = enrollmentService.findAllApprovedEnrollments();
        model.addAttribute("enrollmentRequests", enrollmentRequests);
        return "manager/create-class";
    }

    @GetMapping("manager/create-class/{eid}")
    public String actionCreateClass(@PathVariable("eid") long eid,
                                    @RequestParam String action) {
        Enrollment enrollment = enrollmentService.findById(eid);
        if (enrollment == null) {
            return "redirect:/mentor/schedule?error=enrollmentNotFound";
        }
        if (action.equals("reject")) {
            enrollment.setStatus(Enrollment.EnrollmentStatus.REJECTED);
            enrollmentService.save(enrollment);
            return "redirect:/manager/create-class?status=REJECTED&reject=" + eid;
        }
        if(action.equals("approve")){
            enrollment.setStatus(Enrollment.EnrollmentStatus.CREATED);
            enrollmentScheduleService.saveEnrollmentSchedule(enrollment);
            return "redirect:/manager/create-class?status=APPROVED&approve=" + eid;
        }if(action.equals("view")){
            return "redirect:/manager/create-class/" + eid + "/view";
        }
        return "redirect:/manager/create-class";
    }

    @GetMapping("/manager/create-class/{eid}/view")
    public String viewSensorClass(@PathVariable Long eid,
                                  Model model) {
        Enrollment enrollment = enrollmentService.findById(eid);
        List<Slot> slots = new ArrayList<>();
        List<Day> days = new ArrayList<>();
        String summary = enrollment.getScheduleSummary();

        EnrollmentScheduleServiceImpl.parseDaySlotString(summary, days, slots);

        List<RequestedSchedule> startTime = enrollmentScheduleService.findStartLearningTime(enrollment.getMentee(), enrollment.getCourseMentor(), slots, days, enrollment.getTotalSlots());
        model.addAttribute("startTime", startTime);
        return "manager/skill-register-request-detail";
    }
}

