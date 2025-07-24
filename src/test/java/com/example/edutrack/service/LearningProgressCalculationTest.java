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

// Hàm F4: public int getLearningProgress(UUID menteeId)
// trong DashboardServiceImpl
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
    /**
     * TC 4.1: Kiểm tra trường hợp không có enrollment nào
     * Phân vùng tương đương (Equivalence Partition):
     *   - IP1: Không có enrollment (danh sách rỗng)
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh enrollments.isEmpty() trả về 0
     * Mục đích: Đảm bảo hàm trả về 0% tiến độ học tập trong Youdemi khi không có khóa học nào được đăng ký.
     */
    @Test
    @DisplayName("Should return 0 when no enrollments exist")
    void testNoEnrollments() {
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(Collections.emptyList());
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(0);
    }

    /**
     * TC 4.2: Kiểm tra trường hợp totalSlots bằng 0
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có enrollment hợp lệ
     *   - IP2: totalSlots = 0
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh totalSlots == 0 (trả về 0% tiến độ)
     * Mục đích: Đảm bảo hàm trả về 0% tiến độ học tập trong Youdemi khi tổng số slot của khóa học bằng 0.
     */
    @Test
    @DisplayName("Should return 0 when total slots is 0")
    void testZeroTotalSlots() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(0);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(0);
    }

    /**
     * TC 4.3: Kiểm tra trường hợp không có slot nào được tham gia
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có enrollment hợp lệ
     *   - VP2: totalSlots > 0
     *   - IP3: attendedSlots = 0
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh attendedSlots == 0 (trả về 0% tiến độ)
     * Mục đích: Đảm bảo hàm trả về 0% tiến độ học tập trong Youdemi khi học viên không tham gia bất kỳ slot nào.
     */
    @Test
    @DisplayName("Should return 0% when no sessions attended")
    void testZeroCompleted() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(5);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT, Enrollment.EnrollmentStatus.APPROVED)).thenReturn(0);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(0);
    }

    /**
     * TC 4.4: Kiểm tra trường hợp hoàn thành toàn bộ slot
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có enrollment hợp lệ
     *   - VP2: totalSlots > 0
     *   - VP3: attendedSlots = totalSlots
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh attendedSlots == totalSlots (trả về 100% tiến độ)
     *   - Nhánh tính toán (attendedSlots / totalSlots) * 100
     * Mục đích: Đảm bảo hàm trả về 100% tiến độ học tập trong Youdemi khi học viên tham gia tất cả các slot.
     */
    @Test
    @DisplayName("Should return 100% when all sessions attended")
    void testFullCompletion() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(10);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT, Enrollment.EnrollmentStatus.APPROVED)).thenReturn(10);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(100);
    }

    /**
     * TC 4.5: Kiểm tra trường hợp hoàn thành một nửa số slot
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có enrollment hợp lệ
     *   - VP2: totalSlots > 0
     *   - VP4: 0 < attendedSlots < totalSlots
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh tính toán (attendedSlots / totalSlots) * 100 với kết quả phân số
     * Mục đích: Đảm bảo hàm tính đúng tiến độ học tập (50%) trong Youdemi khi học viên hoàn thành một nửa số slot.
     */
    @Test
    @DisplayName("Should return 50% for half completed")
    void testHalfCompletion() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(10);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT, Enrollment.EnrollmentStatus.APPROVED)).thenReturn(5);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(50);
    }

    /**
     * TC 4.6: Kiểm tra hành vi làm tròn phần trăm
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có enrollment hợp lệ
     *   - VP2: totalSlots > 0
     *   - VP4: 0 < attendedSlots < totalSlots
     *   - VB1: Kết quả tính toán có phần thập phân (55.55%)
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh làm tròn (attendedSlots / totalSlots) * 100
     * Mục đích: Đảm bảo hàm làm tròn đúng tiến độ học tập trong Youdemi (55.55% → 56%) khi kết quả là số thập phân.
     */
    @Test
    @DisplayName("Should correctly round percentage")
    void testRoundingBehavior() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(9);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT, Enrollment.EnrollmentStatus.APPROVED)).thenReturn(5);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(56); // 5/9 = 55.55% -> 56
    }

    /**
     * TC 4.7: Kiểm tra tổng hợp totalSlots từ nhiều enrollment
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP5: Có nhiều enrollment hợp lệ
     *   - VP2: totalSlots > 0 (cho mỗi enrollment)
     *   - VP4: 0 < attendedSlots < totalSlots
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh tổng hợp totalSlots từ nhiều enrollment
     *   - Nhánh tính toán (sum(attendedSlots) / sum(totalSlots)) * 100
     * Mục đích: Đảm bảo hàm tính đúng tiến độ học tập tổng hợp trong Youdemi khi học viên tham gia nhiều khóa học.
     */
    @Test
    @DisplayName("Should sum total slots across multiple enrollments")
    void testMultipleEnrollments() {
        Enrollment e1 = mock(Enrollment.class);
        Enrollment e2 = mock(Enrollment.class);
        when(e1.getTotalSlots()).thenReturn(4);
        when(e2.getTotalSlots()).thenReturn(6);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(e1, e2));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT, Enrollment.EnrollmentStatus.APPROVED)).thenReturn(6);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(60);
    }

    /**
     * TC 4.8: Kiểm tra trường hợp số slot tham gia vượt quá totalSlots (dữ liệu lỗi)
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có enrollment hợp lệ
     *   - VP2: totalSlots > 0
     *   - IP4: attendedSlots > totalSlots
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh xử lý trường hợp attendedSlots > totalSlots (trả về 100%)
     * Mục đích: Đảm bảo hàm trả về 100% tiến độ học tập trong Youdemi khi số slot tham gia vượt quá tổng số slot (trường hợp dữ liệu lỗi).
     */
    @Test
    @DisplayName("Should return 100 if attended is greater than total (data corruption case)")
    void testAttendedExceedsTotal() {
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getTotalSlots()).thenReturn(5);
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(eq(menteeId), any())).thenReturn(List.of(enrollment));
        when(enrollmentScheduleRepository.countAttendedSlotsByMenteeId(menteeId, EnrollmentSchedule.Attendance.PRESENT, Enrollment.EnrollmentStatus.APPROVED)).thenReturn(10);
        int result = dashboardService.getLearningProgress(menteeId);
        assertThat(result).isEqualTo(100);
    }
}