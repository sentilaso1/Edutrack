package com.example.edutrack.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.test.util.ReflectionTestUtils;
import com.example.edutrack.accounts.dto.ContactFormDTO;
import com.example.edutrack.accounts.service.interfaces.SystemConfigService;
import com.example.edutrack.accounts.controller.ContactController;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;

@ExtendWith(MockitoExtension.class)
public class ContactControllerTest {

        @Mock
        private SystemConfigService systemConfigService;

        @Mock
        private JavaMailSender mailSender;

        @Mock
        private RedirectAttributes redirectAttributes;

        @Mock
        private MultipartFile file;

        @Mock
        private MimeMessage mimeMessage;

        @Mock
        private MimeMessageHelper mimeMessageHelper;

        @InjectMocks
        private ContactController contactController;

        private ContactFormDTO validContactForm;

        @BeforeEach
        void setUp() {
                // Inject mailSender manually since it uses @Autowired
                ReflectionTestUtils.setField(contactController, "mailSender", mailSender);

                validContactForm = new ContactFormDTO();
                validContactForm.setEmail("test@example.com");
                validContactForm.setPhone("0123456789");
                validContactForm.setSubject("Test Subject");
                validContactForm.setMessage("Test message content");
        }

        // Test Case 1: Valid contact form with no file attachment - Success path
        @Test
        void testSendContactMail_ValidFormNoFile_Success() throws Exception {
                // Arrange
                when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
                when(mimeMessage.getAllRecipients()).thenReturn(
                                new InternetAddress[] { new InternetAddress("animeismylife789@gmail.com") });

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(mailSender).createMimeMessage();
                verify(mailSender).send(mimeMessage);
                verify(redirectAttributes).addFlashAttribute("success", "Your message has been sent successfully!");
                verify(redirectAttributes, never()).addFlashAttribute(eq("error"), anyString());
        }

        // Test Case 2: Valid contact form with valid file attachment - Success path
        @Test
        void testSendContactMail_ValidFormWithValidFile_Success() throws Exception {
                // Arrange
                when(file.isEmpty()).thenReturn(false);
                when(file.getSize()).thenReturn(1024L); // 1KB - within limit
                when(file.getOriginalFilename()).thenReturn("test.pdf");
                when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
                when(mimeMessage.getAllRecipients()).thenReturn(
                                new InternetAddress[] { new InternetAddress("animeismylife789@gmail.com") });

                // Act
                String result = contactController.sendContactMail(validContactForm, file, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(mailSender).createMimeMessage();
                verify(mailSender).send(mimeMessage);
                verify(redirectAttributes).addFlashAttribute("success", "Your message has been sent successfully!");
        }

        // Test Case 3: Invalid email - null email
        @Test
        void testSendContactMail_NullEmail_Error() {
                // Arrange
                validContactForm.setEmail(null);

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error", "Invalid email address.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 4: Invalid email - invalid format
        @Test
        void testSendContactMail_InvalidEmailFormat_Error() {
                // Arrange
                validContactForm.setEmail("invalid-email");

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error", "Invalid email address.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 5: Invalid phone - null phone
        @Test
        void testSendContactMail_NullPhone_Error() {
                // Arrange
                validContactForm.setPhone(null);

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error", "Invalid phone number.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 6: Invalid phone - wrong format
        @Test
        void testSendContactMail_InvalidPhoneFormat_Error() {
                // Arrange
                validContactForm.setPhone("123456789"); // Missing leading 0

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error", "Invalid phone number.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 7: Invalid subject - null subject
        @Test
        void testSendContactMail_NullSubject_Error() {
                // Arrange
                validContactForm.setSubject(null);

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error", "Subject cannot be empty.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 8: Invalid subject - empty subject
        @Test
        void testSendContactMail_EmptySubject_Error() {
                // Arrange
                validContactForm.setSubject("   "); // Only whitespace

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error", "Subject cannot be empty.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 9: Invalid message - null message
        @Test
        void testSendContactMail_NullMessage_Error() {
                // Arrange
                validContactForm.setMessage(null);

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Message cannot be empty or exceed 1000 characters.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 10: Invalid message - message too long
        @Test
        void testSendContactMail_MessageTooLong_Error() {
                // Arrange
                String longMessage = "a".repeat(1001); // 1001 characters - exceeds limit
                validContactForm.setMessage(longMessage);

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Message cannot be empty or exceed 1000 characters.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 11: Invalid file - file too large
        @Test
        void testSendContactMail_FileTooLarge_Error() {
                // Arrange
                when(file.isEmpty()).thenReturn(false);
                when(file.getSize()).thenReturn(6 * 1024 * 1024L); // 6MB - exceeds 5MB limit

                // Act
                String result = contactController.sendContactMail(validContactForm, file, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Invalid file. File must be less than 5MB and in .pdf, .doc, .docx, or .txt format.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 12: Invalid file - unsupported extension
        @Test
        void testSendContactMail_UnsupportedFileExtension_Error() {
                // Arrange
                when(file.isEmpty()).thenReturn(false);
                when(file.getSize()).thenReturn(1024L);
                when(file.getOriginalFilename()).thenReturn("test.jpg"); // Unsupported extension

                // Act
                String result = contactController.sendContactMail(validContactForm, file, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Invalid file. File must be less than 5MB and in .pdf, .doc, .docx, or .txt format.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 13: Invalid file - null filename
        @Test
        void testSendContactMail_NullFilename_Error() {
                // Arrange
                when(file.isEmpty()).thenReturn(false);
                when(file.getSize()).thenReturn(1024L);
                when(file.getOriginalFilename()).thenReturn(null);

                // Act
                String result = contactController.sendContactMail(validContactForm, file, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Invalid file. File must be less than 5MB and in .pdf, .doc, .docx, or .txt format.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 14: Empty file - should be valid (file is optional)
        @Test
        void testSendContactMail_EmptyFile_Success() throws Exception {
                // Arrange
                when(file.isEmpty()).thenReturn(true);
                when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
                when(mimeMessage.getAllRecipients()).thenReturn(
                                new InternetAddress[] { new InternetAddress("animeismylife789@gmail.com") });

                // Act
                String result = contactController.sendContactMail(validContactForm, file, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(mailSender).createMimeMessage();
                verify(mailSender).send(mimeMessage);
                verify(redirectAttributes).addFlashAttribute("success", "Your message has been sent successfully!");
        }

        // Test Case 15: Exception during email sending
        @Test
        void testSendContactMail_EmailSendingException_Error() throws Exception {
                // Arrange
                when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
                when(mimeMessage.getAllRecipients()).thenReturn(
                                new InternetAddress[] { new InternetAddress("animeismylife789@gmail.com") });
                doThrow(new RuntimeException("Mail server error")).when(mailSender).send(mimeMessage);

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute(eq("error"),
                                eq("Failed to send your message: Mail server error"));
        }

        // Test Case 16: Boundary value - message exactly 1000 characters
        @Test
        void testSendContactMail_MessageExactly1000Chars_Success() throws Exception {
                // Arrange
                String exactMessage = "a".repeat(1000); // Exactly 1000 characters
                validContactForm.setMessage(exactMessage);
                when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
                when(mimeMessage.getAllRecipients()).thenReturn(
                                new InternetAddress[] { new InternetAddress("animeismylife789@gmail.com") });

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(mailSender).createMimeMessage();
                verify(mailSender).send(mimeMessage);
                verify(redirectAttributes).addFlashAttribute("success", "Your message has been sent successfully!");
        }

        // Test Case 17: Boundary value - file exactly 5MB
        @Test
        void testSendContactMail_FileExactly5MB_Success() throws Exception {
                // Arrange
                when(file.isEmpty()).thenReturn(false);
                when(file.getSize()).thenReturn(5 * 1024 * 1024L); // Exactly 5MB
                when(file.getOriginalFilename()).thenReturn("test.pdf");
                when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
                when(mimeMessage.getAllRecipients()).thenReturn(
                                new InternetAddress[] { new InternetAddress("animeismylife789@gmail.com") });

                // Act
                String result = contactController.sendContactMail(validContactForm, file, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(mailSender).createMimeMessage();
                verify(mailSender).send(mimeMessage);
                verify(redirectAttributes).addFlashAttribute("success", "Your message has been sent successfully!");
        }

        // Test Case 18: Valid phone with 10 digits
        @Test
        void testSendContactMail_ValidPhone10Digits_Success() throws Exception {
                // Arrange
                validContactForm.setPhone("0123456789"); // 10 digits
                when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
                when(mimeMessage.getAllRecipients()).thenReturn(
                                new InternetAddress[] { new InternetAddress("animeismylife789@gmail.com") });

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(mailSender).createMimeMessage();
                verify(mailSender).send(mimeMessage);
                verify(redirectAttributes).addFlashAttribute("success", "Your message has been sent successfully!");
        }

        // Test Case 19: Valid phone with 11 digits
        @Test
        void testSendContactMail_ValidPhone11Digits_Success() throws Exception {
                // Arrange
                validContactForm.setPhone("01234567890"); // 11 digits
                when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
                when(mimeMessage.getAllRecipients()).thenReturn(
                                new InternetAddress[] { new InternetAddress("animeismylife789@gmail.com") });

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(mailSender).createMimeMessage();
                verify(mailSender).send(mimeMessage);
                verify(redirectAttributes).addFlashAttribute("success", "Your message has been sent successfully!");
        }

        // Test Case 20: Test all supported file extensions individually
        @Test
        void testSendContactMail_PdfExtension_Success() throws Exception {
                testFileExtension(".pdf");
        }

        @Test
        void testSendContactMail_DocExtension_Success() throws Exception {
                testFileExtension(".doc");
        }

        @Test
        void testSendContactMail_DocxExtension_Success() throws Exception {
                testFileExtension(".docx");
        }

        @Test
        void testSendContactMail_TxtExtension_Success() throws Exception {
                testFileExtension(".txt");
        }

        private void testFileExtension(String extension) throws Exception {
                // Arrange
                when(file.isEmpty()).thenReturn(false);
                when(file.getSize()).thenReturn(1024L);
                when(file.getOriginalFilename()).thenReturn("test" + extension);
                when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
                when(mimeMessage.getAllRecipients()).thenReturn(
                                new InternetAddress[] { new InternetAddress("animeismylife789@gmail.com") });

                // Act
                String result = contactController.sendContactMail(validContactForm, file, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(mailSender).createMimeMessage();
                verify(mailSender).send(mimeMessage);
                verify(redirectAttributes).addFlashAttribute("success", "Your message has been sent successfully!");
        }

        // Test Case 21: Empty message validation
        @Test
        void testSendContactMail_EmptyMessage_Error() {
                // Arrange
                validContactForm.setMessage("   "); // Only whitespace

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error",
                                "Message cannot be empty or exceed 1000 characters.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 22: Phone validation - too short
        @Test
        void testSendContactMail_PhoneTooShort_Error() {
                // Arrange
                validContactForm.setPhone("012345678"); // Only 9 digits

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error", "Invalid phone number.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 23: Phone validation - too long
        @Test
        void testSendContactMail_PhoneTooLong_Error() {
                // Arrange
                validContactForm.setPhone("012345678901"); // 12 digits

                // Act
                String result = contactController.sendContactMail(validContactForm, null, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(redirectAttributes).addFlashAttribute("error", "Invalid phone number.");
                verify(mailSender, never()).send(any(MimeMessage.class));
                verify(mailSender, never()).createMimeMessage();
        }

        // Test Case 24: File case sensitivity test
        @Test
        void testSendContactMail_UpperCaseExtension_Success() throws Exception {
                // Arrange
                when(file.isEmpty()).thenReturn(false);
                when(file.getSize()).thenReturn(1024L);
                when(file.getOriginalFilename()).thenReturn("test.PDF"); // Uppercase extension
                when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
                when(mimeMessage.getAllRecipients()).thenReturn(
                                new InternetAddress[] { new InternetAddress("animeismylife789@gmail.com") });

                // Act
                String result = contactController.sendContactMail(validContactForm, file, redirectAttributes);

                // Assert
                assertEquals("redirect:/", result);
                verify(mailSender).createMimeMessage();
                verify(mailSender).send(mimeMessage);
                verify(redirectAttributes).addFlashAttribute("success", "Your message has been sent successfully!");
        }
}