package com.example.edutrack.timetables.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.timetables.model.Day;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.service.interfaces.EnrollmentScheduleService;
import com.example.edutrack.timetables.service.interfaces.MentorAvailableTimeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MenteeScheduleController {
    private final MentorAvailableTimeService mentorAvailableTimeService;
    private final EnrollmentScheduleService enrollmentScheduleService;

    public MenteeScheduleController(MentorAvailableTimeService mentorAvailableTimeService, EnrollmentScheduleService enrollmentScheduleService) {
        this.mentorAvailableTimeService = mentorAvailableTimeService;
        this.enrollmentScheduleService = enrollmentScheduleService;
    }

    @PostMapping("/register-skill")
    public String registerSkill(@RequestParam String email,
                                @RequestParam String fullName,
                                @RequestParam String phone,
                                @RequestParam Integer age,
                                @RequestParam List<Slot> slot,
                                @RequestParam List<Day> day,
                                @RequestParam CourseMentor courseMentor,
                                @RequestParam Integer totalSlot,
                                HttpSession session,
                                @RequestParam(required = false) String startTime) {
        Mentee user = (Mentee) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if(startTime == null){
            startTime = enrollmentScheduleService.findStartLearningTime(user, courseMentor, slot, day, totalSlot);
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
