package com.example.edutrack.profiles.service;

import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class AutoCVVerificationTest {

    @Mock
    private CvRepository cvRepository;
    @Mock
    private EntityManager entityManager;
    @Mock
    private com.example.edutrack.accounts.repository.UserRepository userRepository;
    @Mock
    private com.example.edutrack.curriculum.repository.CourseRepository courseRepository;
    @Mock
    private com.example.edutrack.curriculum.repository.CVCourseRepository cvCourseRepository;

    @InjectMocks
    private CvServiceImpl cvService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper to make a dummy CV
    private CV makeCV(UUID id, String status) {
        CV cv = new CV();
        cv.setId(id);
        cv.setStatus(status);
        return cv;
    }

    @Test
    void scheduleAIVerification_noPendingCVs_shouldNotCallAIVerification() {
        // Arrange
        when(cvRepository.findByStatus("pending")).thenReturn(Collections.emptyList());

        // Spy to verify aiVerifyCV is not called
        CvServiceImpl cvServiceSpy = Mockito.spy(cvService);

        // Act
        cvServiceSpy.scheduleAIVerification();

        // Assert
        verify(cvRepository, times(1)).findByStatus("pending");
        verify(cvServiceSpy, never()).aiVerifyCV(any(), any());
        assertThat(cvServiceSpy.isBatchRunning()).isFalse();
        assertThat(cvServiceSpy.getLastBatchEnd()).isNotNull();
    }

    @Test
    void scheduleAIVerification_pendingCVsExist_callsAIVerifyCVForEach() {
        // Arrange
        CV cv1 = makeCV(UUID.randomUUID(), "pending");
        CV cv2 = makeCV(UUID.randomUUID(), "pending");
        List<CV> cvList = List.of(cv1, cv2);

        when(cvRepository.findByStatus("pending")).thenReturn(cvList);
        CvServiceImpl cvServiceSpy = Mockito.spy(cvService);

        // Stub getCoursesForCV and aiVerifyCV
        List<Course> mockCourses = List.of(mock(Course.class));
        doReturn(mockCourses).when(cvServiceSpy).getCoursesForCV(any(CV.class));
        doNothing().when(cvServiceSpy).aiVerifyCV(any(CV.class), anyList());

        // Act
        cvServiceSpy.scheduleAIVerification();

        // Assert
        verify(cvRepository, times(1)).findByStatus("pending");
        verify(cvServiceSpy, times(2)).getCoursesForCV(any(CV.class));
        verify(cvServiceSpy, times(2)).aiVerifyCV(any(CV.class), anyList());
        assertThat(cvServiceSpy.isBatchRunning()).isFalse();
        assertThat(cvServiceSpy.getLastBatchEnd()).isNotNull();
    }

    @Test
    void scheduleAIVerification_handlesExceptionAndResetsBatch() {
        // Arrange
        CV cv1 = makeCV(UUID.randomUUID(), "pending");
        when(cvRepository.findByStatus("pending")).thenReturn(List.of(cv1));
        CvServiceImpl cvServiceSpy = Mockito.spy(cvService);
        doThrow(new RuntimeException("AI error")).when(cvServiceSpy).aiVerifyCV(any(CV.class), anyList());
        doReturn(Collections.emptyList()).when(cvServiceSpy).getCoursesForCV(any(CV.class));

        // Act
        try {
            cvServiceSpy.scheduleAIVerification();
        } catch (Exception e) {
            assertThatThrownBy(() -> cvServiceSpy.scheduleAIVerification())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("AI error");
        }

        // Assert: batchRunning should be false even if there was an exception
        assertThat(cvServiceSpy.isBatchRunning()).isFalse();
        assertThat(cvServiceSpy.getLastBatchEnd()).isNotNull();
    }

    @Test
    void scheduleAIVerification_getCoursesForCVThrowsException_batchResets() {
        CV cv1 = makeCV(UUID.randomUUID(), "pending");
        when(cvRepository.findByStatus("pending")).thenReturn(List.of(cv1));
        CvServiceImpl cvServiceSpy = Mockito.spy(cvService);
        doThrow(new RuntimeException("getCourses error")).when(cvServiceSpy).getCoursesForCV(any(CV.class));

        // Act
        try {
            cvServiceSpy.scheduleAIVerification();
        } catch (Exception e) {
            assertThatThrownBy(() -> cvServiceSpy.scheduleAIVerification())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("getCourses error");
        }

        // Assert: batchRunning should be false even if there was an exception
        assertThat(cvServiceSpy.isBatchRunning()).isFalse();
        assertThat(cvServiceSpy.getLastBatchEnd()).isNotNull();
    }

    @Test
    void scheduleAIVerification_findByStatusThrows_batchNeverRunning() {
        when(cvRepository.findByStatus("pending")).thenThrow(new RuntimeException("DB error"));
        CvServiceImpl cvServiceSpy = Mockito.spy(cvService);

        try {
            cvServiceSpy.scheduleAIVerification();
        } catch (Exception e) {
            assertThatThrownBy(() -> cvServiceSpy.scheduleAIVerification())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("DB error");
        }

        // Since the exception is thrown before batchRunning is set, it should still be false.
        assertThat(cvServiceSpy.isBatchRunning()).isFalse();
    }

}
