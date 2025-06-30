package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.curriculum.controller.FeedbackController;
import com.example.edutrack.curriculum.dto.FeedbackDTO;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.service.interfaces.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackControllerTest_SubmitFeedback {
    @Mock
    private MenteeRepository menteeRepository;
    @Mock
    private CourseMentorRepository courseMentorRepository;
    @Mock
    private FeedbackService feedbackService;
    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private FeedbackController feedbackController;

    private UUID menteeId = UUID.randomUUID();
    private UUID courseId = UUID.randomUUID();
    private UUID mentorId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        when(redirectAttributes.addFlashAttribute(anyString(), any())).thenReturn(redirectAttributes);
    }
    @Test
    void testRatingIsNull() {
        Mentee mentee = new Mentee();
        CourseMentor cm = new CourseMentor();
        when(menteeRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        when(courseMentorRepository.findByCourse_IdAndMentor_Id(courseId, mentorId)).thenReturn(Optional.of(cm));

        FeedbackDTO dto = new FeedbackDTO("good", null, menteeId, courseId, mentorId);
        String result = feedbackController.submitFeedback(dto, redirectAttributes);

        assertEquals("redirect:/mentors/" + mentorId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Rating must be between 0 and 5"));
        verifyNoMoreInteractions(feedbackService);
    }

    @Test
    void testRatingBelowMin() {
        Mentee mentee = new Mentee();
        CourseMentor cm = new CourseMentor();
        when(menteeRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        when(courseMentorRepository.findByCourse_IdAndMentor_Id(courseId, mentorId)).thenReturn(Optional.of(cm));

        FeedbackDTO dto = new FeedbackDTO("good", -1.0, menteeId, courseId, mentorId);
        String result = feedbackController.submitFeedback(dto, redirectAttributes);

        assertEquals("redirect:/mentors/" + mentorId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Rating must be between 0 and 5"));
        verifyNoMoreInteractions(feedbackService);
    }

    @Test
    void testRatingAboveMax() {
        Mentee mentee = new Mentee();
        CourseMentor cm = new CourseMentor();
        when(menteeRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        when(courseMentorRepository.findByCourse_IdAndMentor_Id(courseId, mentorId)).thenReturn(Optional.of(cm));

        FeedbackDTO dto = new FeedbackDTO("good", 6.0, menteeId, courseId, mentorId);
        String result = feedbackController.submitFeedback(dto, redirectAttributes);

        assertEquals("redirect:/mentors/" + mentorId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Rating must be between 0 and 5"));
        verifyNoMoreInteractions(feedbackService);
    }

    @Test
    void testContentIsNull() {
        Mentee mentee = new Mentee();
        CourseMentor cm = new CourseMentor();
        when(menteeRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        when(courseMentorRepository.findByCourse_IdAndMentor_Id(courseId, mentorId)).thenReturn(Optional.of(cm));

        FeedbackDTO dto = new FeedbackDTO(null, 4.0, menteeId, courseId, mentorId);
        String result = feedbackController.submitFeedback(dto, redirectAttributes);

        assertEquals("redirect:/mentors/" + mentorId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Content must not be empty"));
        verifyNoMoreInteractions(feedbackService);
    }

    @Test
    void testContentIsEmpty() {
        Mentee mentee = new Mentee();
        CourseMentor cm = new CourseMentor();
        when(menteeRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        when(courseMentorRepository.findByCourse_IdAndMentor_Id(courseId, mentorId)).thenReturn(Optional.of(cm));

        FeedbackDTO dto = new FeedbackDTO("   ", 4.0, menteeId, courseId, mentorId);
        String result = feedbackController.submitFeedback(dto, redirectAttributes);

        assertEquals("redirect:/mentors/" + mentorId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Content must not be empty"));
        verifyNoMoreInteractions(feedbackService);
    }

    @Test
    void testContentTooLong() {
        Mentee mentee = new Mentee();
        CourseMentor cm = new CourseMentor();
        when(menteeRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        when(courseMentorRepository.findByCourse_IdAndMentor_Id(courseId, mentorId)).thenReturn(Optional.of(cm));
        String longContent = "a".repeat(3001);

        FeedbackDTO dto = new FeedbackDTO(longContent, 4.0, menteeId, courseId, mentorId);
        String result = feedbackController.submitFeedback(dto, redirectAttributes);

        assertEquals("redirect:/mentors/" + mentorId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Content is too long"));
        verifyNoMoreInteractions(feedbackService);
    }

    @Test
    void testFeedbackServiceThrows() {
        Mentee mentee = new Mentee();
        CourseMentor cm = new CourseMentor();
        when(menteeRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        when(courseMentorRepository.findByCourse_IdAndMentor_Id(courseId, mentorId)).thenReturn(Optional.of(cm));
        doThrow(new RuntimeException("DB down")).when(feedbackService)
                .submitFeedback(anyString(), anyDouble(), any(), any());

        FeedbackDTO dto = new FeedbackDTO("Good feedback", 4.0, menteeId, courseId, mentorId);
        String result = feedbackController.submitFeedback(dto, redirectAttributes);

        assertEquals("redirect:/mentors/" + mentorId, result);
        verify(redirectAttributes).addFlashAttribute(eq("error"), contains("Could not submit review:"));
    }

    @Test
    void testHappyPath() {
        Mentee mentee = new Mentee();
        CourseMentor cm = new CourseMentor();
        when(menteeRepository.findById(menteeId)).thenReturn(Optional.of(mentee));
        when(courseMentorRepository.findByCourse_IdAndMentor_Id(courseId, mentorId)).thenReturn(Optional.of(cm));

        FeedbackDTO dto = new FeedbackDTO("Great mentor!", 5.0, menteeId, courseId, mentorId);
        String result = feedbackController.submitFeedback(dto, redirectAttributes);

        assertEquals("redirect:/mentors/" + mentorId, result);
        verify(redirectAttributes).addFlashAttribute(eq("success"), contains("Your review was submitted successfully!"));
        verify(feedbackService).submitFeedback(eq("Great mentor!"), eq(5.0), eq(mentee), eq(cm));
    }
}
