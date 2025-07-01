package com.example.edutrack.service;

import com.example.edutrack.curriculum.service.implementation.DashboardServiceImpl;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.repository.EnrollmentRepository;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LearningProgressCalculationTest {

    private EnrollmentRepository enrollmentRepository;
    private EnrollmentScheduleRepository enrollmentScheduleRepository;
    private DashboardServiceImpl dashboardService;

    private UUID menteeId;

    @BeforeEach
    void setUp() {
        enrollmentScheduleRepository = mock(EnrollmentScheduleRepository.class);
        enrollmentRepository = mock(EnrollmentRepository.class);

        dashboardService = new DashboardServiceImpl(
                enrollmentScheduleRepository,
                null,
                enrollmentRepository,
                null,
                null
        );

        menteeId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should return 0 when no enrollments exist")
    void testNoEnrollments() {
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(Collections.emptyList());
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return 0 when total slots is 0")
    void testZeroTotalSlots() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(0);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return 0% when no sessions attended")
    void testZeroCompleted() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(5);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT)).thenReturn(0);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return 100% when all sessions attended")
    void testFullCompletion() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(10);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT)).thenReturn(10);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(100);
    }

    @Test
    @DisplayName("Should return 50% for half completed")
    void testHalfCompletion() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(10);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT)).thenReturn(5);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(50);
    }

    @Test
    @DisplayName("Should correctly round percentage")
    void testRoundingBehavior() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(9);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT)).thenReturn(5);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(56); // 5/9 = 55.55% -> 56
    }

    @Test
    @DisplayName("Should sum total slots across multiple enrollments")
    void testMultipleEnrollments() {
        Enrollment e1 = mock(Enrollment.class);
        Enrollment e2 = mock(Enrollment.class);
        when(e1.getTotalSlots()).thenReturn(4);
        when(e2.getTotalSlots()).thenReturn(6);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(e1, e2));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT)).thenReturn(6);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(60);
    }

    @Test
    @DisplayName("Should return 100 if attended is greater than total (data corruption case)")
    void testAttendedExceedsTotal() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(5);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT)).thenReturn(10);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(100);
    }
}
