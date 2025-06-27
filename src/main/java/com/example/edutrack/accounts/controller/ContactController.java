package com.example.edutrack.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.edutrack.accounts.dto.ContactFormDTO;
import com.example.edutrack.accounts.service.interfaces.SystemConfigService;

import jakarta.mail.internet.MimeMessage;

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
        public String sendContactMail(
        @ModelAttribute ContactFormDTO contactForm,
        @RequestParam(value = "file", required = false) MultipartFile file,
        RedirectAttributes redirectAttributes) {
                try {
                        MimeMessage mimeMessage = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                        helper.setTo("animeismylife789@gmail.com");//tạm thời để test và sẽ thay bằng email của manager
                        helper.setSubject("[Contact] " + contactForm.getSubject());
                        helper.setText("From: " + contactForm.getEmail()
                                + "\nPhone: " + contactForm.getPhone()
                                + "\n\nMessage:\n" + contactForm.getMessage());

                        // Xử lý file đính kèm nếu có
                        if (file != null && !file.isEmpty()) {
                                helper.addAttachment(file.getOriginalFilename(), file);
                        }

                        mailSender.send(mimeMessage);
                        redirectAttributes.addFlashAttribute("success", "Your message has been sent successfully!");

                } catch (Exception ex) {
                        redirectAttributes.addFlashAttribute("error", "Failed to send your message.");
                }

                return "redirect:/";
        }
}
