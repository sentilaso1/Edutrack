package com.example.edutrack.auth.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.auth.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showSignupForm(HttpServletRequest request, Model model) {
        String emailFromCookie = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("email".equals(cookie.getName())) {
                    emailFromCookie = cookie.getValue();
                    model.addAttribute("email", emailFromCookie);
                    break;
                }
            }
        }
        User user = new User();
        user.setGender("male");
        model.addAttribute("user", user);
        return "auth/login_signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("user") User user, Model model) {
        if (userService.isEmailExists(user.getEmail())) {
            model.addAttribute("error", "Email already exists");
            return "auth/login_signup";
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode(user.getPassword());
        user.setPassword(hashed);
        userService.registerUser(user);
        model.addAttribute("user", new User());
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        @RequestParam(required = false) String rememberMe,
                        HttpServletResponse response,
                        HttpSession session,
                        Model model) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(password, user.getPassword())) {
                session.setAttribute("loggedInUser", user);
                if ("on".equals(rememberMe)) {
                    Cookie cookie = new Cookie("email", email);
                    cookie.setMaxAge(7 * 24 * 60 * 60);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                } else {
                    Cookie cookie = new Cookie("email", null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
                return "redirect:/home";
            }
        }
        model.addAttribute("error", "Invalid email or password");
        model.addAttribute("user", new User());
        return "auth/login_signup";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}