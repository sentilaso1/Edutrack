package com.example.edutrack.timetables.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.service.implementation.CourseMentorServiceImpl;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
public class MenteeScheduleController {
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final EnrollmentScheduleService enrollmentScheduleService;
    private final CourseMentorServiceImpl courseMentorService;

    public MenteeScheduleController(MentorAvailableTimeService mentorAvailableTimeService, EnrollmentScheduleService enrollmentScheduleService, CourseMentorServiceImpl courseMentorService) {
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.enrollmentScheduleService = enrollmentScheduleService;
        this.courseMentorService = courseMentorService;
    }

    @PostMapping("/courses/register/{cmid}")
    public String registerSkill(@RequestParam String email,
                                @RequestParam String fullName,
                                @RequestParam String phone,
                                @RequestParam Integer age,
                                @RequestParam List<Slot> slot,
                                @RequestParam List<Day> day,
                                @PathVariable(value = "cmid") UUID courseMentorId,
                                @RequestParam Integer totalSlot,
                                HttpSession session,
                                Model model,
                                @RequestParam(required = false) String startTime) {
        Mentee user = (Mentee) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        CourseMentor courseMentor = courseMentorService.findById(courseMentorId);
        if(startTime == null){
            startTime = enrollmentScheduleService.findStartLearningTime(user, courseMentor, slot, day, totalSlot);
            System.out.println(startTime);
            if(startTime == null){
                startTime = "Cannot find start time";
            }
            model.addAttribute("startTime", startTime);
            model.addAttribute("courseMentor", courseMentor);
            return "register-section";
        }else{
            //dung d
        }

        return "register-section";
    }
}
//fullName=dung+beo
// email=lephuonglinhnga1801gmail.com
// phone=0198332567
// age=19
// experience=beginner
// motivation=12345678
// slot=SLOT_1
// day=MONDAY
