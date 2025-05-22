package com.example.edutrack.accounts.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.accounts.service.implementations.MenteeServiceImpl;
import com.example.edutrack.accounts.service.interfaces.ProfileService;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import com.example.edutrack.accounts.service.interfaces.MenteeService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AccountsController {

        private final UserRepository userRepository;
        private final MenteeRepository menteeRepository;
        private final MentorRepository mentorRepository;
        private final ProfileService userService;
        private final MentorService mentorService;
        private final MenteeService menteeService;

        public AccountsController(ProfileService userService,
                        MentorService mentorService,
                        MenteeService menteeService,
                        UserRepository userRepository,
                        MenteeRepository menteeRepository,
                        MentorRepository mentorRepository) {
                this.userService = userService;
                this.mentorService = mentorService;
                this.menteeService = menteeService;
                this.userRepository = userRepository;
                this.menteeRepository = menteeRepository;
                this.mentorRepository = mentorRepository;
        }

        @GetMapping("/profile/{id}")
        public String viewProfile(@PathVariable String id, Model model) throws IOException {
                User user = userService.getUserById(id);
                model.addAttribute("user", user);
                Mentor mentor = mentorService.getMentorById(id);
                if (mentor != null) {
                        model.addAttribute("role", "mentor");
                        model.addAttribute("mentor", mentor);
                } else {
                        model.addAttribute("role", "mentee");
                        model.addAttribute("mentee", menteeService.getMenteeById(id));
                }
                return "accounts/html/profile";
        }

        public boolean verifyEmail(String email) {
                if (email == null || email.trim().isEmpty())
                        return false;
                EmailValidator validator = EmailValidator.getInstance();
                return validator.isValid(email);
        }

        public boolean verifyPhone(String phone) {
                if (phone == null || phone.trim().isEmpty())
                        return false;
                // Việt Nam: bắt đầu bằng 0, tổng 10 hoặc 11 chữ số (0xxx... hoặc 0xxxxxxxxx)
                String phoneRegex = "^0\\d{9,10}$";
                return phone.matches(phoneRegex);
        }

        @PostMapping("/profile/{id}/edit")
        public String editProfile(@PathVariable String id,
                        @RequestParam String fullName,
                        @RequestParam String email,
                        @RequestParam String phone,
                        @RequestParam String bio,
                        @RequestParam(required = false) String expertise,
                        @RequestParam(required = false) String interests,
                        org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes)
                        throws IOException {
                if (fullName == null || fullName.trim().isEmpty()) {
                        redirectAttributes.addFlashAttribute("error", "Full name is required");
                        return "redirect:/profile/" + id + "#edit";
                }
                if (email == null || email.trim().isEmpty() || !verifyEmail(email)) {
                        redirectAttributes.addFlashAttribute("error", "Email không hợp lệ");
                        return "redirect:/profile/" + id + "#edit";
                }
                if (phone == null || phone.trim().isEmpty() || !verifyPhone(phone)) {
                        redirectAttributes.addFlashAttribute("error", "Phone number không hợp lệ");
                        return "redirect:/profile/" + id + "#edit";
                }

                User user = userService.getUserById(id);
                if (user == null) {
                        throw new IllegalArgumentException("User not found with id: " + id);
                }
                Mentor mentor = mentorService.getMentorById(id);
                if ((expertise == null || expertise.trim().isEmpty()) && mentor != null) {
                        redirectAttributes.addFlashAttribute("error", "Expertise is required");
                        return "redirect:/profile/" + id + "#edit";
                }
                user.setFullName(fullName.trim());
                user.setEmail(email.trim());
                user.setPhone(phone);
                user.setBio(bio != null ? bio.trim() : null);
                userRepository.save(user);

                if (mentor != null) {
                        mentor.setExpertise(expertise);
                        mentorRepository.save(mentor);
                } else {
                        Mentee mentee = menteeService.getMenteeById(id);
                        mentee.setInterests(interests);
                        menteeRepository.save(mentee);
                }
                return "redirect:/profile/" + id;
        }

        @PostMapping("/upload/{id}")
        public String uploadAvatar(@PathVariable String id, @RequestParam("image") MultipartFile file)
                        throws IOException {
                userService.updateAvatar(id, file);
                return "redirect:/profile/" + id;
        }

        @GetMapping("/avatar/{id}")
        public void getAvatar(@PathVariable String id, HttpServletResponse response) throws IOException {
                User user = userService.getUserById(id);
                if (user != null && user.getAvatar() != null) {
                        response.setContentType("image/jpeg");
                        response.getOutputStream().write(user.getAvatar());
                        response.getOutputStream().close();
                }
        }

}
