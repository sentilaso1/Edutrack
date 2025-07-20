package com.example.edutrack.service;


import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.common.service.interfaces.LLMService;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.repository.CvRepository;
import com.example.edutrack.profiles.service.implementations.CvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CvServiceTest_AIScheduling {

    @InjectMocks
    private CvServiceImpl cvService;

    @Mock
    private CvRepository cvRepository;
    @Mock
    private MentorRepository mentorRepository;


    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(cvService, "batchRunning", false);
        ReflectionTestUtils.setField(cvService, "lastBatchStart", null);
        ReflectionTestUtils.setField(cvService, "lastBatchEnd", null);
    }

    @Test
    void F1_shouldNotCallAiVerifyCV_WhenNoPendingCVs() {

        when(cvRepository.findByStatus("pending")).thenReturn(Collections.emptyList());
        CvServiceImpl spyService = Mockito.spy(cvService);

        spyService.scheduleAIVerification();

        verify(spyService, never()).aiVerifyCV(any(), anyString());
        assertFalse(spyService.isBatchRunning());
        assertNotNull(spyService.getLastBatchEnd());
    }

    @Test
    void F2_shouldCallAiVerifyCV_ForEachPendingCV() {
        // Arrange
        CV cv1 = new CV();
        CV cv2 = new CV();
        List<CV> pendingCVs = List.of(cv1, cv2);
        when(cvRepository.findByStatus("pending")).thenReturn(pendingCVs);
        CvServiceImpl spyService = Mockito.spy(cvService);

        doNothing().when(spyService).aiVerifyCV(any(), anyString());

        spyService.scheduleAIVerification();

        verify(spyService, times(2)).aiVerifyCV(any(), anyString());
        assertFalse(spyService.isBatchRunning());
        assertNotNull(spyService.getLastBatchEnd());
    }

    @Test
    void F3_shouldResetBatchAndThrow_WhenAiVerifyCVThrows() {
        // Arrange
        CV cv = new CV();
        when(cvRepository.findByStatus("pending")).thenReturn(List.of(cv));
        CvServiceImpl spyService = Mockito.spy(cvService);

        doThrow(new RuntimeException("AI Error")).when(spyService).aiVerifyCV(any(), anyString());

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, spyService::scheduleAIVerification);
        assertEquals("AI Error", thrown.getMessage());
        assertFalse(spyService.isBatchRunning());
        assertNotNull(spyService.getLastBatchEnd());
    }

    @Test
    void F4_shouldResetBatchAndThrow_WhenGetCoursesForCVThrows() {
        CV cv = new CV();
        cv.setId(UUID.randomUUID());
        when(cvRepository.findByStatus("pending")).thenReturn(List.of(cv));

        CvServiceImpl spyService = Mockito.spy(cvService);
        doAnswer(inv -> {
            spyService.processCombinedAIResponse(cv, """
                {
                  "choices": [{
                    "message": {
                      "content": "{ \\"is-approve\\": \\"false\\", \\"reason\\": \\"invalid\\" }"
                    }
                  }]
                }
            """);
            return null;
        }).when(spyService).aiVerifyCV(any(), anyString());

        when(mentorRepository.findById(any())).thenThrow(new RuntimeException("Repo error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, spyService::scheduleAIVerification);
        assertEquals("Repo error", thrown.getMessage());
        assertFalse(spyService.isBatchRunning());
        assertNotNull(spyService.getLastBatchEnd());
    }

    @Test
    void F5_shouldNotSetBatchRunning_WhenFindByStatusThrows() {
        when(cvRepository.findByStatus("pending")).thenThrow(new RuntimeException("DB Error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> cvService.scheduleAIVerification());
        assertEquals("DB Error", thrown.getMessage());
        assertFalse(cvService.isBatchRunning());
        assertNotNull(cvService.getLastBatchEnd());
    }
}
