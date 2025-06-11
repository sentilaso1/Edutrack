package com.example.edutrack.auth.controller;

import com.example.edutrack.accounts.model.User;
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

    @GetMapping("/login/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        String url = googleOAuthService.buildAuthorizationUrl();
        response.sendRedirect(url);
    }

    @GetMapping("/oauth2/callback")
    public String oauth2Callback(@RequestParam String code,
                                 HttpSession session) {
        String accessToken = googleOAuthService.exchangeCodeForAccessToken(code);

        Map<String, Object> userInfo = googleOAuthService.fetchUserInfo(accessToken);
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFullName(name);
                    newUser.setPassword("");
                    newUser.setIsActive(true);
                    newUser.setIsLocked(false);
                    newUser.setGender("unknown");
                    newUser.setPhone("unknown");
                    try {
                        return userRepository.save(newUser);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                });

        session.setAttribute("loggedInUser", user);

        return "redirect:/home";
    }
}
