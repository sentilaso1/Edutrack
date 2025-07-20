package com.example.edutrack.service;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.repository.CourseRepository;
import com.example.edutrack.profiles.dto.CVForm;
import com.example.edutrack.profiles.dto.CourseApplicationDetail;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.service.implementations.CvServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class CvServiceTest_ValidateAndApplyCourseDetails {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private MentorRepository mentorRepository;

    @InjectMocks
    private CvServiceImpl cvService;

    private final UUID mentorId = UUID.randomUUID();
    private final CV dummyCv = mock(CV.class);

    @Test
    void detailsIsNull_returnsEarly() {
        CVForm cvRequest = mock(CVForm.class);
        when(cvRequest.getCourseDetails()).thenReturn(null);

        assertDoesNotThrow(() ->
                cvService.validateAndApplyCourseDetails(cvRequest, dummyCv, mentorId)
        );
    }

    @Test
    void detailsIsEmpty_returnsEarly() {
        CVForm cvRequest = mock(CVForm.class);
        when(cvRequest.getCourseDetails()).thenReturn(Collections.emptyMap());

        assertDoesNotThrow(() ->
                cvService.validateAndApplyCourseDetails(cvRequest, dummyCv, mentorId)
        );
    }

    @Test
    void invalidPrice_null_throws() {
        UUID courseId = UUID.randomUUID();
        CourseApplicationDetail detail = new CourseApplicationDetail();
        detail.setDescription("valid description");

        Map<UUID, CourseApplicationDetail> details = new HashMap<>();
        details.put(courseId, detail);

        CVForm cvRequest = mock(CVForm.class);
        when(cvRequest.getCourseDetails()).thenReturn(details);

        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(new Mentor()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                cvService.validateAndApplyCourseDetails(cvRequest, dummyCv, mentorId)
        );
        assertTrue(ex.getMessage().startsWith("Invalid price"));
    }

    @Test
    void invalidPrice_zeroOrNegative_throws() {
        UUID courseId = UUID.randomUUID();
        CourseApplicationDetail detail = new CourseApplicationDetail();
        detail.setDescription("valid description");

        Map<UUID, CourseApplicationDetail> details = new HashMap<>();
        details.put(courseId, detail);

        CVForm cvRequest = mock(CVForm.class);
        when(cvRequest.getCourseDetails()).thenReturn(details);

        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(new Mentor()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                cvService.validateAndApplyCourseDetails(cvRequest, dummyCv, mentorId)
        );
        assertTrue(ex.getMessage().startsWith("Invalid price"));
    }

    @Test
    void invalidDescription_null_throws() {
        UUID courseId = UUID.randomUUID();
        CourseApplicationDetail detail = new CourseApplicationDetail();
        detail.setDescription(null);

        Map<UUID, CourseApplicationDetail> details = new HashMap<>();
        details.put(courseId, detail);

        CVForm cvRequest = mock(CVForm.class);
        when(cvRequest.getCourseDetails()).thenReturn(details);

        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(new Mentor()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                cvService.validateAndApplyCourseDetails(cvRequest, dummyCv, mentorId)
        );
        assertTrue(ex.getMessage().startsWith("Missing description"));
    }

    @Test
    void invalidDescription_blank_throws() {
        UUID courseId = UUID.randomUUID();
        CourseApplicationDetail detail = new CourseApplicationDetail();
        detail.setDescription("   ");

        Map<UUID, CourseApplicationDetail> details = new HashMap<>();
        details.put(courseId, detail);

        CVForm cvRequest = mock(CVForm.class);
        when(cvRequest.getCourseDetails()).thenReturn(details);

        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(new Mentor()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                cvService.validateAndApplyCourseDetails(cvRequest, dummyCv, mentorId)
        );
        assertTrue(ex.getMessage().startsWith("Missing description"));
    }

    @Test
    void courseNotFound_throws() {
        UUID courseId = UUID.randomUUID();
        CourseApplicationDetail detail = new CourseApplicationDetail();
        detail.setDescription("valid");

        Map<UUID, CourseApplicationDetail> details = new HashMap<>();
        details.put(courseId, detail);

        CVForm cvRequest = mock(CVForm.class);
        when(cvRequest.getCourseDetails()).thenReturn(details);

        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(new Mentor()));
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                cvService.validateAndApplyCourseDetails(cvRequest, dummyCv, mentorId)
        );
        assertTrue(ex.getMessage().startsWith("Course not found"));
    }

    @Test
    void allValid_callsHandleCourseMentorLogic() {
        UUID courseId = UUID.randomUUID();
        CourseApplicationDetail detail = new CourseApplicationDetail();
        detail.setDescription("OK");

        Map<UUID, CourseApplicationDetail> details = new HashMap<>();
        details.put(courseId, detail);

        CVForm cvRequest = mock(CVForm.class);
        when(cvRequest.getCourseDetails()).thenReturn(details);

        Mentor mentor = new Mentor();
        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(mentor));
        Course course = mock(Course.class);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        CvServiceImpl spyService = Mockito.spy(cvService);
        doNothing().when(spyService).handleCourseMentorLogic(any(), any(), any(), any());

        spyService.validateAndApplyCourseDetails(cvRequest, dummyCv, mentorId);

        verify(spyService).handleCourseMentorLogic(eq(dummyCv), eq(mentor), eq(course), eq(detail));
    }
}
