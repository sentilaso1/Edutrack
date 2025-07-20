package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.profiles.controller.CvController;
import com.example.edutrack.profiles.dto.CVForm;
import com.example.edutrack.profiles.service.interfaces.CvService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class CvControllerTest_HandleCVFormTest {

    @InjectMocks
    private CvController cvController;

    @Mock
    private CvService cvService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @Mock
    private CVForm cvForm;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(UUID.randomUUID());
    }

    @Test
    void shouldRedirectTo404_whenParseSelectedCoursesFails() {

        String result = cvController.handleCVFormSubmission(cvForm, model, session);

        verify(model).addAttribute(eq("error"), anyString());
        assertEquals("redirect:/404", result);
    }

    @Test
    void shouldRedirectToLogin_whenUserIsNotInSession() {
        when(session.getAttribute("loggedInUser")).thenReturn(null);

        String result = cvController.handleCVFormSubmission(cvForm, model, session);

        assertEquals("redirect:/login", result);
    }

    @Test
    void shouldRedirectToMentor_whenCVCreationIsSuccessful() {
        when(session.getAttribute("loggedInUser")).thenReturn(user);

        String result = cvController.handleCVFormSubmission(cvForm, model, session);

        verify(cvService).createCV(cvForm, user.getId());
        verify(model).addAttribute("message", "CV created successfully!");
        assertEquals("redirect:/mentor", result);
    }

    @Test
    void shouldRedirectTo404_whenCVCreationFails() {
        when(session.getAttribute("loggedInUser")).thenReturn(user);
        doThrow(new RuntimeException("DB error")).when(cvService).createCV(any(), any());

        String result = cvController.handleCVFormSubmission(cvForm, model, session);

        verify(model).addAttribute(eq("error"), contains("Failed to create CV"));
        assertEquals("redirect:/404", result);
    }
}
