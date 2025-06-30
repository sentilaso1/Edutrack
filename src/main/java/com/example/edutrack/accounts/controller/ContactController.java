package com.example.edutrack.accounts.controller;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/contact")
public class ContactController {
        private static final Logger logger = LoggerFactory.getLogger(ContactController.class);
        private final SystemConfigService systemConfigService;

        @Autowired
        public ContactController(SystemConfigService systemConfigService) {
                this.systemConfigService = systemConfigService;
        }

        @Autowired
        private JavaMailSender mailSender;

        public boolean verifyEmail(String email) {
                if (email == null || email.trim().isEmpty())
                        return false;
                EmailValidator validator = EmailValidator.getInstance();
                return validator.isValid(email);
        }

        public boolean verifyPhone(String phone) {
                if (phone == null || phone.trim().isEmpty())
                        return false;
                String phoneRegex = "^0\\d{9,10}$";
                return phone.matches(phoneRegex);
        }

        public boolean verifyMessage(String message) {
                if (message == null || message.trim().isEmpty()) {
                        return false;
                }
                return message.length() <= 1000;
        }

        public boolean verifyFile(MultipartFile file) {
                if (file == null || file.isEmpty()) {
                        return true; // File is optional
                }
                long maxFileSize = 5 * 1024 * 1024;
                if (file.getSize() > maxFileSize) {
                        return false;
                }
                String[] allowedExtensions = { ".pdf", ".doc", ".docx", ".txt" };
                String fileName = file.getOriginalFilename();
                if (fileName == null) {
                        return false;
                }
                for (String ext : allowedExtensions) {
                        if (fileName.toLowerCase().endsWith(ext)) {
                                return true;
                        }
                }
                return false;
        }

        @PostMapping("/send")
        public String sendContactMail(
                        @ModelAttribute ContactFormDTO contactForm,
                        @RequestParam(value = "file", required = false) MultipartFile file,
                        RedirectAttributes redirectAttributes) {
                try {
                        if (contactForm.getEmail() == null || !verifyEmail(contactForm.getEmail())) {
                                redirectAttributes.addFlashAttribute("error", "Invalid email address.");
                                return "redirect:/";
                        }
                        if (contactForm.getPhone() == null || !verifyPhone(contactForm.getPhone())) {
                                redirectAttributes.addFlashAttribute("error", "Invalid phone number.");
                                return "redirect:/";
                        }
                        if (contactForm.getSubject() == null || contactForm.getSubject().trim().isEmpty()) {
                                redirectAttributes.addFlashAttribute("error", "Subject cannot be empty.");
                                return "redirect:/";
                        }
                        if (contactForm.getMessage() == null || !verifyMessage(contactForm.getMessage())) {
                                redirectAttributes.addFlashAttribute("error",
                                                "Message cannot be empty or exceed 1000 characters.");
                                return "redirect:/";
                        }
                        if (!verifyFile(file)) {
                                redirectAttributes.addFlashAttribute("error",
                                                "Invalid file. File must be less than 5MB and in .pdf, .doc, .docx, or .txt format.");
                                return "redirect:/";
                        }

                        MimeMessage mimeMessage = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                        helper.setTo("animeismylife789@gmail.com"); // Consider making this configurable
                        helper.setSubject("[Contact] " + contactForm.getSubject());
                        helper.setText("From: " + contactForm.getEmail()
                                        + "\nPhone: " + contactForm.getPhone()
                                        + "\n\nMessage:\n" + contactForm.getMessage());

                        if (file != null && !file.isEmpty()) {
                                helper.addAttachment(file.getOriginalFilename(), file);
                        }

                        logger.debug("Attempting to send email to: {}", helper.getMimeMessage().getAllRecipients()[0]);
                        mailSender.send(mimeMessage);
                        redirectAttributes.addFlashAttribute("success", "Your message has been sent successfully!");

                } catch (Exception ex) {
                        logger.error("Failed to send email: {}", ex.getMessage(), ex);
                        redirectAttributes.addFlashAttribute("error",
                                        "Failed to send your message: " + ex.getMessage());
                }

                return "redirect:/";
        }
}