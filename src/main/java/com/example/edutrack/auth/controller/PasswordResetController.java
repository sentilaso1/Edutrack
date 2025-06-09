package com.example.edutrack.auth.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.edutrack.accounts.service.interfaces.SystemConfigService;
import java.util.Optional;
import java.util.UUID;

@Controller
public class PasswordResetController {
    private final UserService userService;
    private final JavaMailSender mailSender;
    private final SystemConfigService systemConfigService;

    @Autowired
    public PasswordResetController(UserService userService, JavaMailSender mailSender, 
                                   SystemConfigService systemConfigService) {
        this.userService = userService;
        this.mailSender = mailSender;
        this.systemConfigService = systemConfigService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOpt = userService.findByEmail(email);
        
        if (userOpt.isPresent()) {
            String token = UUID.randomUUID().toString();
            User user = userOpt.get();
            user.setResetToken(token);
            userService.registerUser(user);

            String resetLink = "http://localhost:6969/reset-password?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom(systemConfigService.getValue("app.email"));
            message.setSubject("Reset Password");
            message.setText("Click this link to reset your password: " + resetLink);

            mailSender.send(message);
            model.addAttribute("message", "Reset link sent to your email");
        } else {
            model.addAttribute("message", "Email not found");
        }

        return "auth/forgot_password";
    }

    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam("token") String token, Model model) {
        Optional<User> userOpt = userService.findByResetToken(token);
        
        if (userOpt.isPresent()) {
            model.addAttribute("token", token);
            return "auth/reset_password";
        }

        model.addAttribute("message", "Invalid token");
        return "auth/forgot_password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       Model model) {
        Optional<User> userOpt = userService.findByResetToken(token);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            user.setResetToken(null);
            userService.registerUser(user);
            return "redirect:/login?resetSuccess";
        }

        model.addAttribute("message", "Invalid token");
        return "auth/reset_password";
    }
}
