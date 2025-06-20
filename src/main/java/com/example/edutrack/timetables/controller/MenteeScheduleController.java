package com.example.edutrack.timetables.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.service.interfaces.MenteeService;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.service.implementation.CourseMentorServiceImpl;
import com.example.edutrack.timetables.dto.EnrollmentRequestDTO;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import com.example.edutrack.transactions.model.Transaction;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.interfaces.TransactionService;
import com.example.edutrack.transactions.service.interfaces.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class MenteeScheduleController {
    private final MenteeService menteeService;
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final EnrollmentService enrollmentService;
    private final EnrollmentScheduleService enrollmentScheduleService;
    private final CourseMentorServiceImpl courseMentorService;
    private final WalletService walletService;
    private final TransactionService transactionService;

    public MenteeScheduleController(MenteeService menteeService, MentorAvailableTimeService mentorAvailableTimeService, EnrollmentService enrollmentService, EnrollmentScheduleService enrollmentScheduleService, CourseMentorServiceImpl courseMentorService, WalletService walletService, TransactionService transactionService) {
        this.menteeService = menteeService;
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.enrollmentService = enrollmentService;
        this.enrollmentScheduleService = enrollmentScheduleService;
        this.courseMentorService = courseMentorService;
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @PostMapping("/courses/checkout/{cmid}")
    public String handleCheckout(
            @PathVariable(value = "cmid") UUID courseMentorId,
            @ModelAttribute EnrollmentRequestDTO params,
            HttpSession session,
            Model model
    ) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Mentee> menteeOpt = menteeService.findById(user.getId());
        if (menteeOpt.isEmpty()) {
            model.addAttribute("error", "Mentee not found");
            return "/checkout/checkout-info";
        }

        CourseMentor courseMentor = courseMentorService.findById(courseMentorId);
        if (courseMentor == null) {
            model.addAttribute("error", "Course Mentor not found");
            return "/checkout/checkout-info";
        }
        model.addAttribute("course", courseMentor.getCourse());
        model.addAttribute("mentor", courseMentor.getMentor());

        if (params.getTotalSlot() == null || params.getSlot() == null || params.getDay() == null) {
            model.addAttribute("error", "Invalid enrollment parameters");
            return "/checkout/checkout-info";
        }

        Optional<Wallet> walletOptional = walletService.findById(user.getId());
        if (walletOptional.isEmpty()) {
            walletOptional = Optional.of(walletService.save(user));
        }
        Wallet wallet = walletOptional.get();
        model.addAttribute("wallet", wallet);

        Double totalCost = courseMentor.getPrice() * params.getTotalSlot();
        if (wallet.getBalance() <= 0 || wallet.getBalance() < totalCost) {
            model.addAttribute("error", "Insufficient balance in wallet");
            return "/checkout/checkout-info";
        }

        String startTime = enrollmentScheduleService.findStartLearningTime(
                menteeOpt.get(),
                courseMentor,
                params.getSlot(),
                params.getDay(),
                params.getTotalSlot()
        );
        if (startTime == null) {
            model.addAttribute("error", "Cannot find start time for the selected schedule");
            return "/checkout/checkout-info";
        }

        wallet.setBalance(wallet.getBalance() - totalCost);
        wallet.setOnHold(wallet.getOnHold() + totalCost);
        walletService.save(wallet);

        Transaction transaction = new Transaction(
                totalCost,
                "Checkout for Course Mentor: " + courseMentor.getId(),
                wallet
        );
        transaction.setCourse(courseMentor.getCourse());
        transactionService.save(transaction);

        Enrollment enrollment = new Enrollment(
                menteeOpt.get(),
                courseMentor,
                params.getTotalSlot(),
                params.getSlot(),
                params.getDay(),
                startTime
        );
        enrollmentService.save(enrollment);

        model.addAttribute("success", "Checkout Successful");
        return "/checkout/checkout-info";
    }

    @PostMapping("/courses/register/{cmid}")
    public String registerSkill(@RequestParam List<Slot> slot,
                                @RequestParam List<Day> day,
                                @PathVariable(value = "cmid") UUID courseMentorId,
                                @RequestParam Integer totalSlot,
                                HttpSession session,
                                Model model,
                                @RequestParam String action) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Mentee> menteeOpt = menteeService.findById(user.getId());
        if (menteeOpt.isEmpty()) {
            model.addAttribute("error", "Mentee not found");
            return "register-section";
        }
        Mentee mentee = menteeOpt.get();

        Optional<Wallet> walletOptional = walletService.findById(user.getId());
        if (walletOptional.isEmpty()) {
            walletOptional = Optional.of(walletService.save(user));
        }
        model.addAttribute("wallet", walletOptional.get());

        CourseMentor courseMentor = courseMentorService.findById(courseMentorId);
        if ("checkStartDate".equals(action)) {
            String startTime = enrollmentScheduleService.findStartLearningTime(mentee, courseMentor, slot, day, totalSlot);
            System.out.println(startTime);
            if (startTime == null) {
                startTime = "Cannot find start time";
            }
            model.addAttribute("startTime", startTime);
            session.setAttribute("startTime", startTime);
            return "redirect:/courses/register/{cmid}";
        }

        return "register-section";
    }
}