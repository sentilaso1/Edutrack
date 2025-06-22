package com.example.edutrack.auth.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.auth.service.interfaces.GoogleOAuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
public class GoogleAuthController {

    @Autowired
    private GoogleOAuthService googleOAuthService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenteeRepository menteeRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @GetMapping("/login/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        String url = googleOAuthService.buildAuthorizationUrl();
        response.sendRedirect(url);
    }

    @GetMapping("/oauth2/callback")
    public String oauth2Callback(@RequestParam String code, HttpSession session) {
        String accessToken = googleOAuthService.exchangeCodeForAccessToken(code);
        Map<String, Object> userInfo = null;
        if (accessToken != null) {
            try {
                userInfo = googleOAuthService.fetchUserInfo(accessToken);
            } catch (Exception e) {
                userInfo = null;
            }
        }
        if (userInfo == null) {
            return "redirect:/404";
        }
        session.setAttribute("googleUserInfo", userInfo);
        String email = (String) userInfo.get("email");

        if (menteeRepository.findByEmail(email).isPresent()) {
            Mentee mentee = menteeRepository.findByEmail(email).get();
            session.setAttribute("loggedInUser", mentee);
            session.setAttribute("role", "mentee");
            return "redirect:/home";
        } else if (mentorRepository.findByEmail(email).isPresent()) {
            Mentor mentor = mentorRepository.findByEmail(email).get();
            session.setAttribute("loggedInUser", mentor);
            session.setAttribute("role", "mentor");
            return "redirect:/mentor";
        }
        return "redirect:/choose-role";
    }
}
