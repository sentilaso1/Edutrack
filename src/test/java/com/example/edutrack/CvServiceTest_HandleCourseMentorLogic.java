package com.example.edutrack;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.curriculum.model.ApplicationStatus;
import com.example.edutrack.curriculum.model.CVCourse;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CVCourseRepository;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.profiles.dto.CourseApplicationDetail;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.service.CvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CvServiceTest_HandleCourseMentorLogic {
    @Mock
    private CVCourseRepository cvCourseRepository;
    @Mock
    private CourseMentorRepository courseMentorRepository;

    @InjectMocks
    private CvServiceImpl cvService;

    private CV cv;
    private Mentor mentor;
    private Course course;
    private CourseApplicationDetail detail;

    @BeforeEach
    void setUp() {
        cv = new CV();
        mentor = new Mentor();
        course = new Course();
        detail = new CourseApplicationDetail();
        detail.setPrice(100.0);
        detail.setDescription("desc");
    }

    @Test
    void courseMentorDoesNotExist_createsAndSaves() {
        when(courseMentorRepository.findByMentorAndCourse(mentor, course)).thenReturn(Optional.empty());

        cvService.handleCourseMentorLogic(cv, mentor, course, detail);

        verify(cvCourseRepository).save(argThat(cvc -> cvc.getCv().equals(cv) && cvc.getCourse().equals(course)));
        verify(courseMentorRepository).save(argThat(cm ->
                cm.getMentor().equals(mentor) &&
                        cm.getCourse().equals(course) &&
                        cm.getPrice().equals(100.0) &&
                        cm.getDescription().equals("desc") &&
                        cm.getStatus() == ApplicationStatus.PENDING &&
                        cm.getAppliedDate() != null
        ));
    }

    @Test
    void courseMentorExistsRejected_updatesAndSaves() {
        CourseMentor existing = new CourseMentor();
        existing.setStatus(ApplicationStatus.REJECTED);
        existing.setPrice(50.0);
        existing.setDescription("old");
        when(courseMentorRepository.findByMentorAndCourse(mentor, course)).thenReturn(Optional.of(existing));

        cvService.handleCourseMentorLogic(cv, mentor, course, detail);

        verify(cvCourseRepository).save(any(CVCourse.class));
        assertEquals(100.0, existing.getPrice());
        assertEquals("desc", existing.getDescription());
        assertEquals(ApplicationStatus.PENDING, existing.getStatus());
        assertNotNull(existing.getAppliedDate());
        verify(courseMentorRepository).save(existing);
    }

    @Test
    void courseMentorExistsNotRejected_skipsUpdateAndSave() {
        CourseMentor existing = new CourseMentor();
        existing.setStatus(ApplicationStatus.ACCEPTED);
        when(courseMentorRepository.findByMentorAndCourse(mentor, course)).thenReturn(Optional.of(existing));

        cvService.handleCourseMentorLogic(cv, mentor, course, detail);

        verify(cvCourseRepository).save(any(CVCourse.class));

        verify(courseMentorRepository, never()).save(existing);
    }
}
