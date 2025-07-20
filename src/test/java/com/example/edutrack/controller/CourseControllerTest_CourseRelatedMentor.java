package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.controller.CourseController;
import com.example.edutrack.curriculum.model.ApplicationStatus;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest_CourseRelatedMentor {

    @InjectMocks
    CourseController courseController;

    @Mock
    CourseRepository courseRepository;

    @Mock
    CourseMentorRepository courseMentorRepository;

    @Mock
    Model model;

    UUID courseId = UUID.randomUUID();

    @Test
    void F1_shouldRetrieveCourseByCourseId() {
        Course course = new Course();
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        courseController.courseMentorList(courseId, model);

        verify(courseRepository).findById(courseId);
    }

    @Test
    void F2_shouldHandleMissingCourseGracefully() {
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            courseController.courseMentorList(courseId, model);
        });
    }

    @Test
    void F3_shouldRetrieveAllCourseMentorsByCourseId() {
        Course course = new Course();
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMentorRepository.findByCourse_IdAndStatus(courseId, ApplicationStatus.ACCEPTED))
                .thenReturn(Collections.emptyList());

        courseController.courseMentorList(courseId, model);

        verify(courseMentorRepository).findByCourse_IdAndStatus(courseId, ApplicationStatus.ACCEPTED);
    }

    @Test
    void F4_shouldExtractUniqueMentorsFromCourseMentors() {
        Course course = new Course();
        course.setId(courseId);

        Mentor m1 = new Mentor(); m1.setFullName("A");
        Mentor m2 = new Mentor(); m2.setFullName("B");

        CourseMentor cm1 = new CourseMentor(); cm1.setMentor(m1);
        CourseMentor cm2 = new CourseMentor(); cm2.setMentor(m2);
        CourseMentor cm3 = new CourseMentor(); cm3.setMentor(m1); // duplicate

        List<CourseMentor> courseMentors = List.of(cm1, cm2, cm3);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMentorRepository.findByCourse_IdAndStatus(courseId, ApplicationStatus.ACCEPTED))
                .thenReturn(courseMentors);

        ArgumentCaptor<List<Mentor>> captor = ArgumentCaptor.forClass(List.class);

        courseController.courseMentorList(courseId, model);

        verify(model).addAttribute(eq("relatedMentors"), captor.capture());
        List<Mentor> result = captor.getValue();

        assertEquals(2, result.size());
        assertTrue(result.contains(m1));
        assertTrue(result.contains(m2));
    }

    @Test
    void F5_shouldAddRelatedMentorsToModel() {
        Course course = new Course();
        course.setId(courseId);

        Mentor m1 = new Mentor(); m1.setFullName("A");
        CourseMentor cm = new CourseMentor(); cm.setMentor(m1);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMentorRepository.findByCourse_IdAndStatus(courseId, ApplicationStatus.ACCEPTED))
                .thenReturn(List.of(cm));

        courseController.courseMentorList(courseId, model);

        verify(model).addAttribute(eq("relatedMentors"), argThat(list ->
                ((List<?>) list).contains(m1)
        ));
    }

    @Test
    void F6_shouldAddCourseToModel() {
        Course course = new Course();
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMentorRepository.findByCourse_IdAndStatus(any(), any()))
                .thenReturn(Collections.emptyList());

        courseController.courseMentorList(courseId, model);

        verify(model).addAttribute("course", course);
    }

    @Test
    void F7_shouldReturnCorrectViewName() {
        Course course = new Course();
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMentorRepository.findByCourse_IdAndStatus(any(), any()))
                .thenReturn(Collections.emptyList());

        String result = courseController.courseMentorList(courseId, model);
        assertEquals("course-related-mentor", result);
    }

    @Test
    void F8_shouldHandleNoMentorsGracefully() {
        Course course = new Course();
        course.setId(courseId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseMentorRepository.findByCourse_IdAndStatus(courseId, ApplicationStatus.ACCEPTED))
                .thenReturn(Collections.emptyList());

        ArgumentCaptor<List<Mentor>> mentorCaptor = ArgumentCaptor.forClass(List.class);

        courseController.courseMentorList(courseId, model);

        verify(model).addAttribute(eq("relatedMentors"), mentorCaptor.capture());
        assertTrue(mentorCaptor.getValue().isEmpty());
    }

}
