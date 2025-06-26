package com.example.edutrack.auth.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@Controller
public class RoleSelectionController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenteeRepository menteeRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @GetMapping("/choose-role")
    public String showRoleSelection() {
        return "auth/choose-role";
    }

    @PostMapping("/choose-role")
    public String setRole(@RequestParam String role, HttpSession session) {
        Map<String, Object> userInfo = (Map<String, Object>) session.getAttribute("googleUserInfo");
        if (userInfo == null) {
            return "redirect:/login?error";
        }

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");


        if ("MENTEE".equalsIgnoreCase(role)) {
            Mentee mentee = new Mentee();
            mentee.setEmail(email);
            mentee.setPassword("");
            mentee.setFullName(name);
            mentee.setPhone("unknown");
            mentee.setGender("unknown");
            mentee.setIsActive(true);
            mentee.setIsLocked(false);
            mentee.setTotalSessions(0);
            mentee.setInterests("");
            menteeRepository.save(mentee);
            session.setAttribute("loggedInUser", mentee);
            session.setAttribute("role", "mentee");
            session.removeAttribute("googleUserInfo");
            return "redirect:/";
        }
        else if ("MENTOR".equalsIgnoreCase(role)) {
            Mentor mentor = new Mentor();
            mentor.setEmail(email);
            mentor.setPassword("");
            mentor.setFullName(name);
            mentor.setPhone("unknown");
            mentor.setGender("unknown");
            mentor.setIsActive(true);
            mentor.setIsLocked(false);
            mentor.setAvailable(true);
            mentor.setTotalSessions(0);
            mentor.setExpertise("");
            mentor.setRating(0.0);
            mentorRepository.save(mentor);
            session.setAttribute("loggedInUser", mentor);
            session.setAttribute("role", "mentor");
            session.removeAttribute("googleUserInfo");
            return "redirect:/mentor";
        }

        session.removeAttribute("googleUserInfo");
        return "redirect:/home";
    }
}
