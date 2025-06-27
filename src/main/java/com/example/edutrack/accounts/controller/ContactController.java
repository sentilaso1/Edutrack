package com.example.edutrack.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.edutrack.accounts.dto.ContactFormDTO;
import com.example.edutrack.accounts.service.interfaces.SystemConfigService;

@Controller
@RequestMapping("/contact")
public class ContactController {
        private final SystemConfigService systemConfigService;

        @Autowired
        public ContactController(SystemConfigService systemConfigService) {
                this.systemConfigService = systemConfigService;
        }

        @Autowired
        private JavaMailSender mailSender;

        @PostMapping("/send")
        public String sendContactMail(@ModelAttribute ContactFormDTO contactForm,
                        RedirectAttributes redirectAttributes) {
                try {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(contactForm.getEmail());
                        message.setFrom(systemConfigService.getValue("app.email"));
                        message.setSubject("[Contact] " + contactForm.getSubject());
                        message.setText("From: " + contactForm.getEmail() + "\nPhone: " + contactForm.getPhone()
                                        + "\n\nMessage:\n" + contactForm.getMessage());

                        mailSender.send(message);

                        redirectAttributes.addFlashAttribute("success", "Your message has been sent!");
                } catch (Exception ex) {
                        redirectAttributes.addFlashAttribute("error", "Failed to send message.");
                }

                return "redirect:/";
        }
}
