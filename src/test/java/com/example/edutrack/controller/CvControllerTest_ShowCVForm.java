package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
import com.example.edutrack.curriculum.service.interfaces.CourseService;
import com.example.edutrack.profiles.controller.CvController;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CvControllerTest_ShowCVForm {

    @Mock
    private CvRepository cvRepository;
    @Mock
    private CourseService courseService;
    @Mock
    private CourseMentorService courseMentorService;
    @Mock
    private Model model;
    @Mock
    private HttpSession session;
    @InjectMocks
    private CvController cvController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRedirectTo404_whenPageIsInvalid_showCVForm() {
        String view = cvController.showCVForm(model, 0, 1, 3, session);
        assertEquals("redirect:/404", view);
    }

    @Test
    void shouldRedirectToLogin_whenUserNotInSession_showCVForm() {
        when(session.getAttribute("loggedInUser")).thenReturn(null);
        String view = cvController.showCVForm(model, 1, 1, 3, session);
        assertEquals("redirect:/login", view);
    }

    @Test
    void shouldReturnCreateCV_whenNoExistingCV_showCVForm() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(session.getAttribute("loggedInUser")).thenReturn(user);
        when(cvRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(courseMentorService.findByMentorId(userId)).thenReturn(Collections.emptyList());
        when(courseService.findAllExcludingIds(anyList(), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(courseMentorService.findByMentorIdPaged(eq(userId), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        String view = cvController.showCVForm(model, 1, 1, 3, session);
        assertEquals("cv/create-cv", view);
    }

    @Test
    void shouldReturnCreateCV_whenExistingCVPresent_showCVForm() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        CV cv = new CV();
        cv.setStatus("approved");

        when(session.getAttribute("loggedInUser")).thenReturn(user);
        when(cvRepository.findByUserId(userId)).thenReturn(Optional.of(cv));
        when(courseMentorService.findByMentorId(userId)).thenReturn(Collections.emptyList());
        when(courseService.findAllExcludingIds(anyList(), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(courseMentorService.findByMentorIdPaged(eq(userId), any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));

        String view = cvController.showCVForm(model, 1, 1, 3, session);
        assertEquals("cv/create-cv", view);
    }
}

