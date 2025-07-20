package com.example.edutrack.timetables.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.service.interfaces.MenteeService;
import com.example.edutrack.common.service.implementations.EmailService;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.service.implementation.CourseMentorServiceImpl;

import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.EnrollmentService;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import com.example.edutrack.transactions.model.Transaction;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.TransactionService;
import com.example.edutrack.transactions.service.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


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
    private final EmailService emailService;

    public MenteeScheduleController(MenteeService menteeService, MentorAvailableTimeService mentorAvailableTimeService, EnrollmentService enrollmentService, EnrollmentScheduleService enrollmentScheduleService, CourseMentorServiceImpl courseMentorService, WalletService walletService, TransactionService transactionService, EmailService emailService) {
        this.menteeService = menteeService;
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.enrollmentService = enrollmentService;
        this.enrollmentScheduleService = enrollmentScheduleService;
        this.courseMentorService = courseMentorService;
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.emailService = emailService;
    }

    @PostMapping("/courses/checkout/{cmid}")
    public String handleCheckout(
            @PathVariable(value = "cmid") UUID courseMentorId,
            HttpSession session,
            @RequestParam List<Slot> slot,
            @RequestParam List<String> date,
            @RequestParam String totalPrice,
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

        if (slot == null || date == null || totalPrice == null) {
            model.addAttribute("error", "Invalid enrollment parameters");
            return "/checkout/checkout-info";
        }

        Optional<Wallet> walletOptional = walletService.findById(user.getId());
        if (walletOptional.isEmpty()) {
            walletOptional = Optional.of(walletService.save(user));
        }
        Wallet wallet = walletOptional.get();
        model.addAttribute("wallet", wallet);

        Double totalCost = Double.parseDouble(totalPrice);
        if (wallet.getBalance() <= 0 || wallet.getBalance() < totalCost) {
            model.addAttribute("error", "Insufficient balance in wallet");
            return "/checkout/checkout-info";
        }

        wallet.setBalance(wallet.getBalance() - totalCost);
        wallet.setOnHold(wallet.getOnHold() + totalCost);
        walletService.save(wallet);

        Transaction transaction = new Transaction(
                -totalCost,
                "Checkout for Course " + courseMentor.getCourse().getName() + " by " + courseMentor.getMentor().getFullName(),
                wallet
        );
        transactionService.save(transaction);

        List<LocalDate> localDates = date.stream().map(LocalDate::parse).toList();

        Enrollment enrollment = new Enrollment(
                menteeOpt.get(),
                courseMentor,
                slot.size(),
                slot,
                localDates,
                ""
        );
        enrollment.setTransaction(transaction);
        enrollmentService.save(enrollment);

        model.addAttribute("success", "Checkout Successful");
        String emailBody = """
        Dear %s,

        Mentee: %s
        Course: %s

        Click link to view detail:
        http://localhost:6969/mentor/censor-class/%d/view
        """.formatted(
                courseMentor.getMentor().getFullName(),
                menteeOpt.get().getFullName(),
                courseMentor.getCourse().getName(),
                enrollment.getId()
        );

        emailService.sendSimpleMail(
                "lephuonglinhnga1801@gmail.com",
                "EduTrack: New Request Registration",
                emailBody
        );

        return "/checkout/checkout-info";
    }
}