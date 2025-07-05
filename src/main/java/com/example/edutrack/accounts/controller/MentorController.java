package com.example.edutrack.accounts.controller;

import com.example.edutrack.accounts.service.MentorService;
import com.example.edutrack.accounts.dto.IncomeStatsDTO;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.timetables.dto.MentorAvailableSlotDTO;
import com.example.edutrack.timetables.dto.MentorAvailableTimeDTO;
import com.example.edutrack.timetables.dto.RequestedSchedule;
import com.example.edutrack.timetables.model.*;
import com.example.edutrack.timetables.service.implementation.EnrollmentScheduleServiceImpl;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import com.example.edutrack.transactions.model.Transaction;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.TransactionService;
import com.example.edutrack.transactions.service.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller(value = "mentor")
public class MentorController {
    private final EnrollmentScheduleService enrollmentScheduleService;
    private final EnrollmentService enrollmentService;
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final MentorService mentorService;

    @Autowired
    public MentorRepository mentorRepository;

    @Autowired
    public CourseMentorRepository courseMentorRepository;

    @Autowired
    public MentorController(EnrollmentScheduleService enrollmentScheduleService,
                            EnrollmentService enrollmentService,
                            MentorAvailableTimeService mentorAvailableTimeService, WalletService walletService, TransactionService transactionService,
                            MentorService mentorService) {
        this.enrollmentScheduleService = enrollmentScheduleService;
        this.enrollmentService = enrollmentService;
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.mentorService = mentorService;
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

    @GetMapping("/mentor/censor-class")
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

    @GetMapping("/mentor/schedule/mark-as-test-day")
    public String markAsTestDay(@RequestParam Integer esid, HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        EnrollmentSchedule enrollmentSchedule = enrollmentScheduleService.findById(esid);
        if (enrollmentSchedule == null) {
            return "redirect:/mentor/schedule?error=enrollmentNotFound";
        }
        enrollmentSchedule.setTest(!enrollmentSchedule.getTest());
        enrollmentScheduleService.save(enrollmentSchedule);
        return "redirect:/mentor/schedule/" + enrollmentSchedule.getId();
    }

    @GetMapping("/mentor/schedule/save")
    public String submitAttendance(@RequestParam("esid") Integer esid,
                                   @ModelAttribute EnrollmentSchedule enrollmentScheduleFromForm) {

        EnrollmentSchedule enrollmentSchedule = enrollmentScheduleService.findById(esid);
        if (enrollmentSchedule == null) {
            return "redirect:/mentor/schedule?error=enrollmentNotFound";
        }

        LocalDate currentDate = LocalDate.now();
        if (enrollmentSchedule.getDate() != null && enrollmentSchedule.getDate().isBefore(currentDate)) {
            return "redirect:/mentor/schedule/" + esid + "?error=toolatetochange";
        }

        // Update relevant fields only
        enrollmentSchedule.setAttendance(enrollmentScheduleFromForm.getAttendance());
        enrollmentSchedule.setScore(enrollmentScheduleFromForm.getScore());
        enrollmentSchedule.setTitleSection(enrollmentScheduleFromForm.getTitleSection());
        enrollmentSchedule.setDescription(enrollmentScheduleFromForm.getDescription());
        enrollmentSchedule.setReport(enrollmentScheduleFromForm.getReport());

        enrollmentScheduleService.save(enrollmentSchedule);
        return "redirect:/mentor/schedule/" + enrollmentSchedule.getId();
    }


    @GetMapping("/mentor/censor-class/{eid}")
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
        Optional<Wallet> menteeWalletOpt = walletService.findByUser(enrollment.getMentee());
        if (menteeWalletOpt.isEmpty()) {
            return "redirect:/mentor/censor-class?error=menteeWalletNotFound";
        }
        Wallet menteeWallet = menteeWalletOpt.get();

        Optional<Wallet> mentorWalletOpt = walletService.findByUser(mentor);
        if (mentorWalletOpt.isEmpty()) {
            mentorWalletOpt = Optional.of(walletService.save(mentor));
        }
        Wallet mentorWallet = mentorWalletOpt.get();

        if (action.equals("reject")) {
            enrollment.setStatus(Enrollment.EnrollmentStatus.REJECTED);
            enrollmentService.save(enrollment);

            menteeWallet.setOnHold(menteeWallet.getOnHold() - enrollment.getTransaction().getAbsoluteAmount());
            menteeWallet.setBalance(menteeWallet.getBalance() + enrollment.getTransaction().getAbsoluteAmount());
            walletService.save(menteeWallet);

            enrollment.getTransaction().setStatus(Transaction.TransactionStatus.FAILED);
            transactionService.save(enrollment.getTransaction());

            return "redirect:/mentor/censor-class?status=REJECTED&reject=" + eid;
        }

        if("rejectAll".equals(action)){
            List<Enrollment> duplicatedEnrollments = enrollmentService.getDuplicatedSchedules(enrollment);
            for (Enrollment duplicatedEnrollment : duplicatedEnrollments) {
                duplicatedEnrollment.setStatus(Enrollment.EnrollmentStatus.REJECTED);
                enrollmentService.save(enrollment);
            }
            return "redirect:/mentor/censor-class/" + eid + "/view?action=rejected_all";
        }

        if (action.equals("approve")) {
            List<Enrollment> duplicatedEnrollment = enrollmentService.getDuplicatedSchedules(enrollment);
            if (!duplicatedEnrollment.isEmpty()) {
                return "redirect:/mentor/censor-class/" + eid + "/view?approve=fail";
            }
            enrollment.setStatus(Enrollment.EnrollmentStatus.APPROVED);
            enrollmentService.save(enrollment);
            enrollmentScheduleService.saveEnrollmentSchedule(enrollment);

            menteeWallet.setOnHold(menteeWallet.getOnHold() - enrollment.getTransaction().getAbsoluteAmount());
            mentorWallet.setBalance(mentorWallet.getBalance() + enrollment.getTransaction().getAbsoluteAmount());
            walletService.save(menteeWallet);
            walletService.save(mentorWalletOpt.get());

            Transaction transaction = new Transaction(
                    enrollment.getTransaction().getAbsoluteAmount(),
                    "Registration for Course " + enrollment.getCourseMentor().getCourse().getName() + " by " + enrollment.getCourseMentor().getMentor().getFullName(),
                    mentorWallet
            );
            transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
            transactionService.save(transaction);

            enrollment.getTransaction().setStatus(Transaction.TransactionStatus.COMPLETED);
            transactionService.save(enrollment.getTransaction());

            return "redirect:/mentor/censor-class?status=APPROVED&approve=" + eid;
        }
        if (action.equals("view")) {
            return "redirect:/mentor/censor-class/" + eid + "/view";
        }
        return "redirect:/mentor/censor-class";
    }

    @GetMapping("/mentor/censor-class/{eid}/view")
    public String viewSensorClass(@PathVariable Long eid,
                                  Model model,
                                  HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        Enrollment enrollment = enrollmentService.findById(eid);
        String summary = enrollment.getScheduleSummary();
        List<Enrollment> duplicatedEnrollment = enrollmentService.getDuplicatedSchedules(enrollment);
        model.addAttribute("duplicatedEnrollments", duplicatedEnrollment);
        List<RequestedSchedule> startTime = enrollmentScheduleService.findStartLearningTime(summary);
        model.addAttribute("startTime", startTime);
        model.addAttribute("enrollment", enrollment);
        return "mentor/skill-register-request-detail";
    }

    @GetMapping("/mentor/working-date")
    public String workingDate(Model model,
                              @RequestParam(required = false) String end,
                              @RequestParam(defaultValue = "PENDING") String status,
                              HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }
        MentorAvailableTime.Status enumValue = MentorAvailableTime.Status.valueOf(status);

        List<MentorAvailableTimeDTO> setTime = mentorAvailableTimeService.findAllDistinctStartEndDates(mentor, enumValue);
        model.addAttribute("setTime", setTime);

        if ("REJECTED".equals(status) || "DRAFT".equals(status)) {
            if (setTime.size() == 1) {
                LocalDate startDate = setTime.get(0).getStartDate();
                LocalDate endDate = setTime.get(0).getEndDate();
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate endLocal;
        if (end == null || end.isEmpty()) {
            endLocal = mentorAvailableTimeService.findMaxEndDateByStatus(mentor, enumValue);
        } else {
            endLocal = LocalDate.parse(end, formatter);
        }

        if ("REJECTED".equals(status)) {
            List<MentorAvailableTime> foundMentorAvailableTime = mentorAvailableTimeService.findAllMentorAvailableTimeByEndDate(mentor, endLocal);
            if (!foundMentorAvailableTime.isEmpty()) {
                model.addAttribute("reason", foundMentorAvailableTime.get(0).getReason());
            }
        }

        List<MentorAvailableSlotDTO> setSlots = mentorAvailableTimeService.findAllSlotByEndDate(mentor, endLocal);
        boolean[][] slotDayMatrix = setSlots == null || setSlots.isEmpty() ? null : mentorAvailableTimeService.slotDayMatrix(setSlots);

        model.addAttribute("slotDayMatrix", slotDayMatrix);
        model.addAttribute("activeStatus", status.toUpperCase());
        model.addAttribute("slots", Slot.values());
        model.addAttribute("days", Day.values());

        return "/mentor/mentor-working-date";
    }


    @GetMapping("/mentor/income-stats")
    public String mentorStats(Model model, HttpSession session) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        IncomeStatsDTO incomeStats = mentorService.getIncomeStats(mentor.getId());
        model.addAttribute("incomeStats", incomeStats);
        return "accounts/html/mentor-stats";
    }

    @GetMapping("mentor/classes")
    public String mentorClasses(Model model,
                                HttpSession session,
                                @RequestParam(defaultValue = "ONGOING") String status) {
        Mentor mentor = (Mentor) session.getAttribute("loggedInUser");
        if (mentor == null) {
            return "redirect:/login";
        }

        if ("ONGOING".equals(status)) {
            List<Enrollment> ongoingEnrollments = enrollmentService.findOngoingEnrollments(mentor.getId());
            model.addAttribute("ongoingEnrollments", ongoingEnrollments);
        }

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

    @GetMapping("/mentors/{id}/courses")
    public String mentorRelatedCourses(@PathVariable UUID id, Model model) {
        Optional<Mentor> mentorOpt = mentorRepository.findById(id);
        if (mentorOpt.isPresent()) {
            List<CourseMentor> courseMentors = courseMentorRepository.findAllByMentor(mentorOpt.get());
            model.addAttribute("courseMentors", courseMentors);
        }
        return "/mentee/mentor-related-courses";
    }
}
