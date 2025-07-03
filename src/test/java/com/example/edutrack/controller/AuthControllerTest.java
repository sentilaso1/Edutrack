package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.service.interfaces.StaffService;
import com.example.edutrack.auth.controller.AuthController;
import com.example.edutrack.auth.service.RecaptchaService;
import com.example.edutrack.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import static org.mockito.ArgumentMatchers.any;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthControllerTest {
    private AuthController authController;

    private RecaptchaService recaptchaService;
    private UserService userService;
    private StaffService staffService;
    private HttpServletRequest request;
    private Model model;

    private User user;

    @BeforeEach
    void setup() {
        recaptchaService = Mockito.mock(RecaptchaService.class);
        userService = Mockito.mock(UserService.class);
        staffService = Mockito.mock(StaffService.class);
        request = Mockito.mock(HttpServletRequest.class);
        model = Mockito.mock(Model.class);

        authController = new AuthController(userService, staffService, recaptchaService);

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFullName("Test User");
        user.setPhone("0123456789");

        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -20);
        user.setBirthDate(c.getTime());
    }
    //processSignUp

    //TC 2.1 - null captcha
    @Test
    void captchaNull_shouldFail() {
        String result = authController.processSignup(user, "", "password123", "mentee", request, model);
        assertEquals("auth/signup", result);
        verify(model).addAttribute("error", "captcha null");
    }

    //TC 2.2 - invalid captcha
    @Test
    void captchaInvalid_shouldFail() {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(recaptchaService.verify("abc", "127.0.0.1")).thenReturn(false);
        String result = authController.processSignup(user, "abc", "password123", "mentee", request, model);
        assertEquals("auth/signup", result);
        verify(model).addAttribute("error", "Please verify you are not a robot");
    }

    // TC 2.3 - email existed
    @Test
    void emailExists_shouldFail() {
        mockValidCaptcha();
        when(userService.isEmailExists("test@example.com")).thenReturn(true);
        String result = authController.processSignup(user, "abc", "password123", "mentee", request, model);
        assertEquals("auth/signup", result);
        verify(model).addAttribute("error", "Email already exists");
    }

    // TC 2.4 - password mismatch
    @Test
    void passwordMismatch_shouldFail() {
        mockValidCaptcha();
        when(userService.isEmailExists("test@example.com")).thenReturn(false);
        String result = authController.processSignup(user, "abc", "wrongpass", "mentee", request, model);
        assertEquals("auth/signup", result);
        verify(model).addAttribute("error", "Repeated password does not match original password");
    }

    // TC 2.5 - string password length < 6
    @Test
    void passwordTooShort_shouldFail() {
        user.setPassword("123");
        mockValidCaptcha();
        when(userService.isEmailExists(user.getEmail())).thenReturn(false);
        String result = authController.processSignup(user, "abc", "123", "mentee", request, model);
        assertEquals("auth/signup", result);
        verify(model).addAttribute("error", "Password must be at least 6 characters");
    }

    // TC 2.6 - string fullname < 2 words
    @Test
    void fullNameTooShort_shouldFail() {
        user.setFullName("Single");
        mockValidCaptcha();
        when(userService.isEmailExists(user.getEmail())).thenReturn(false);
        String result = authController.processSignup(user, "abc", user.getPassword(), "mentee", request, model);
        assertEquals("auth/signup", result);
        verify(model).addAttribute("error", "Full name must contain at least two words");
    }

    // TC 2.7 - invalid phone number
    @Test
    void invalidPhone_shouldFail() {
        user.setPhone("12345");
        mockValidCaptcha();
        when(userService.isEmailExists(user.getEmail())).thenReturn(false);
        String result = authController.processSignup(user, "abc", user.getPassword(), "mentee", request, model);
        assertEquals("auth/signup", result);
        verify(model).addAttribute("error", "Phone number must be 10 digits");
    }

    // TC 2.8 - age < 6
    @Test
    void underage_shouldFail() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -5); // 5 tuổi
        user.setBirthDate(calendar.getTime());
        mockValidCaptcha();
        when(userService.isEmailExists(user.getEmail())).thenReturn(false);
        String result = authController.processSignup(user, "abc", user.getPassword(), "mentee", request, model);
        assertEquals("auth/signup", result);
        verify(model).addAttribute("error", "You must be at least 6 years old");
    }

    // TC 2.9 - valid sign up with role mentee
    @Test
    void signupSuccess_mentee() {
        mockValidCaptcha();
        when(userService.isEmailExists(user.getEmail())).thenReturn(false);
        String result = authController.processSignup(user, "abc", user.getPassword(), "mentee", request, model);
        assertEquals("redirect:/login", result);
        verify(userService).registerMentee(any(User.class));
    }

    //TC 2.10 - valid sign up with role mentor
    @Test
    void signupSuccess_mentor() {
        mockValidCaptcha();
        when(userService.isEmailExists(user.getEmail())).thenReturn(false);
        String result = authController.processSignup(user, "abc", user.getPassword(), "mentor", request, model);
        assertEquals("redirect:/login", result);
        verify(userService).registerMentor(any(User.class));
    }

    private void mockValidCaptcha() {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(recaptchaService.verify("abc", "127.0.0.1")).thenReturn(true);
    }

    //TC 2.11 - full name exactly 2 words
    @Test
    void fullNameExactlyTwoWords_shouldPass() {
        user.setFullName("John Doe"); // Exactly 2 words
        mockValidCaptcha();
        when(userService.isEmailExists(user.getEmail())).thenReturn(false);

        String result = authController.processSignup(user, "abc", user.getPassword(), "mentee", request, model);
        assertEquals("redirect:/login", result);
        verify(userService).registerMentee(any(User.class));
    }

    //TC 2.12 - phone number exactly 10 digits
    @Test
    void phoneNumberExactly10Digits_shouldPass() {
        user.setPhone("0123456789");
        mockValidCaptcha();
        when(userService.isEmailExists(user.getEmail())).thenReturn(false);

        String result = authController.processSignup(user, "abc", user.getPassword(), "mentee", request, model);
        assertEquals("redirect:/login", result);
        verify(userService).registerMentee(any(User.class));
    }

    //TC 2.13 age exactly 6
    @Test
    void ageExactly6YearsOld_shouldPass() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -6); // Exactly 6 years old
        user.setBirthDate(calendar.getTime());
        mockValidCaptcha();
        when(userService.isEmailExists(user.getEmail())).thenReturn(false);

        String result = authController.processSignup(user, "abc", user.getPassword(), "mentee", request, model);
        assertEquals("redirect:/login", result);
        verify(userService).registerMentee(any(User.class));
    }


}
