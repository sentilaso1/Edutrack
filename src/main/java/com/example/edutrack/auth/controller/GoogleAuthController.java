package com.example.edutrack.auth.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.service.interfaces.MenteeService;
import com.example.edutrack.accounts.service.interfaces.MentorService;
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

    private final GoogleOAuthService googleOAuthService;
    private final MenteeService menteeService;
    private final MentorService mentorService;

    public GoogleAuthController(GoogleOAuthService googleOAuthService,
                                MenteeService menteeService,
                                MentorService mentorService){
        this.googleOAuthService = googleOAuthService;
        this.menteeService = menteeService;
        this.mentorService = mentorService;
    }


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
        if (userInfo == null || userInfo.get("email") == null) {
            return "redirect:/404";
        }
        session.setAttribute("googleUserInfo", userInfo);
        String email = (String) userInfo.get("email");

        if (menteeService.findByEmail(email).isPresent()) {
            Mentee mentee = menteeService.findByEmail(email).get();
            session.setAttribute("loggedInUser", mentee);
            session.setAttribute("role", "mentee");
            return "redirect:/";
        } else if (mentorService.findByEmail(email).isPresent()) {
            Mentor mentor = mentorService.findByEmail(email).get();
            session.setAttribute("loggedInUser", mentor);
            session.setAttribute("role", "mentor");
            return "redirect:/mentor";
        }
        return "redirect:/choose-role";
    }
}
