package com.example.edutrack.service;

import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.GoalRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.implementation.DashboardServiceImpl;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.model.Slot;
import com.example.edutrack.timetables.repository.EnrollmentRepository;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// Hàm F5: public String getNextSessionTime(UUID menteeId)
// trong DashboardServiceImpl
class MenteeDashboardServiceImplTest {

    private EnrollmentScheduleRepository enrollmentScheduleRepository;
    private MentorRepository mentorRepository;
    private EnrollmentRepository enrollmentRepository;
    private TagRepository tagRepository;
    private GoalRepository goalRepository;

    private DashboardServiceImpl dashboardService;

    private UUID menteeId;

    @BeforeEach
    void setUp() {
        enrollmentScheduleRepository = mock(EnrollmentScheduleRepository.class);
        mentorRepository = mock(MentorRepository.class);
        enrollmentRepository = mock(EnrollmentRepository.class);
        tagRepository = mock(TagRepository.class);
        goalRepository = mock(GoalRepository.class);

        dashboardService = new DashboardServiceImpl(
                enrollmentScheduleRepository,
                mentorRepository,
                enrollmentRepository,
                tagRepository,
                goalRepository
        );

        menteeId = UUID.randomUUID();
    }

    /**
     * TC 5.1: Kiểm tra trường hợp danh sách lịch học rỗng
     * Phân vùng tương đương (Equivalence Partition):
     *   - IP1: Không có lịch học (danh sách rỗng)
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh schedules.isEmpty() trả về "No upcoming session"
     * Mục đích: Đảm bảo hàm trả về "No upcoming session" trong Youdemi khi học viên không có lịch học nào.
     */
    @Test
    @DisplayName("Should return 'No upcoming session' when list is empty")
    void testEmptyScheduleList() {
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(Collections.emptyList());

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).isEqualTo("No upcoming session");
    }

    /**
     * TC 5.2: Kiểm tra trường hợp tất cả lịch học đều trong quá khứ
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có lịch học hợp lệ
     *   - IP2: Tất cả lịch học có ngày giờ nhỏ hơn thời điểm hiện tại
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh kiểm tra schedule.getDateTime() < currentDateTime (trả về "No upcoming session")
     * Mục đích: Đảm bảo hàm trả về "No upcoming session" trong Youdemi khi tất cả lịch học đều đã qua.
     */
    @Test
    @DisplayName("Should return 'No upcoming session' when all schedules are in the past")
    void testAllSchedulesInPast() {
        EnrollmentSchedule past = mockSchedule(LocalDate.now().minusDays(1), LocalTime.of(9, 0), "Java");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(past));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).isEqualTo("No upcoming session");
    }

    /**
     * TC 5.3: Kiểm tra trường hợp có một lịch học sắp tới
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có lịch học hợp lệ
     *   - VP2: Có lịch học trong tương lai
     *   - VP3: Chỉ có một lịch học
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh tìm lịch học sớm nhất (chỉ có một lịch)
     *   - Nhánh định dạng chuỗi đầu ra với tên khóa học
     * Mục đích: Đảm bảo hàm trả về thông tin lịch học sắp tới trong Youdemi khi chỉ có một lịch học trong tương lai.
     */
    @Test
    @DisplayName("Should return one upcoming session correctly")
    void testOneUpcomingSchedule() {
        EnrollmentSchedule future = mockSchedule(LocalDate.now().plusDays(1), LocalTime.of(10, 0), "Python");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(future));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).contains("Python");
    }

    /**
     * TC 5.4: Kiểm tra chọn lịch học sớm nhất khi có nhiều lịch học sắp tới
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có lịch học hợp lệ
     *   - VP2: Có nhiều lịch học trong tương lai
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh so sánh và chọn schedule.getDateTime() sớm nhất
     *   - Nhánh định dạng chuỗi đầu ra với tên khóa học
     * Mục đích: Đảm bảo hàm trả về thông tin lịch học sớm nhất trong Youdemi khi có nhiều lịch học trong tương lai.
     */
    @Test
    @DisplayName("Should return earliest upcoming session when multiple exist")
    void testMultipleUpcomingSchedules() {
        EnrollmentSchedule future1 = mockSchedule(LocalDate.now().plusDays(2), LocalTime.of(11, 0), "Java");
        EnrollmentSchedule future2 = mockSchedule(LocalDate.now().plusDays(1), LocalTime.of(8, 0), "Python");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(future1, future2));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).contains("Python");
    }

    /**
     * TC 5.5: Kiểm tra trường hợp có cả lịch học trong quá khứ và tương lai
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có lịch học hợp lệ
     *   - VP2: Có lịch học trong tương lai
     *   - VP4: Có lịch học trong quá khứ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh lọc bỏ lịch học trong quá khứ
     *   - Nhánh chọn lịch học sớm nhất trong tương lai
     * Mục đích: Đảm bảo hàm chỉ trả về lịch học sớm nhất trong tương lai trong Youdemi khi có cả lịch học trong quá khứ và tương lai.
     */
    @Test
    @DisplayName("Should handle mix of past and future schedules")
    void testMixedSchedules() {
        EnrollmentSchedule past = mockSchedule(LocalDate.now().minusDays(1), LocalTime.of(10, 0), "C++");
        EnrollmentSchedule future = mockSchedule(LocalDate.now().plusDays(1), LocalTime.of(9, 0), "Java");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(past, future));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).contains("Java");
    }

    /**
     * TC 5.6: Kiểm tra lịch học tại thời điểm hiện tại (giá trị biên)
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có lịch học hợp lệ
     *   - IP2: Lịch học có ngày giờ nhỏ hơn hoặc bằng thời điểm hiện tại
     *   - VB1: Thời điểm lịch học ngay trước thời điểm hiện tại (giới hạn biên)
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh schedule.getDateTime() <= currentDateTime (trả về "No upcoming session")
     * Mục đích: Đảm bảo hàm coi lịch học ngay trước thời điểm hiện tại là quá khứ và trả về "No upcoming session" trong Youdemi.
     */
    @Test
    @DisplayName("Should handle exact current datetime as past")
    void testScheduleAtCurrentTime() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        EnrollmentSchedule nowSchedule = mockSchedule(today, now.minusMinutes(1), "NodeJS");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(nowSchedule));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).isEqualTo("No upcoming session");
    }

    /**
     * TC 5.7: Kiểm tra định dạng chuỗi đầu ra
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có lịch học hợp lệ
     *   - VP2: Có lịch học trong tương lai
     *   - VP3: Chỉ có một lịch học
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh định dạng chuỗi đầu ra với tên khóa học và thời gian
     * Mục đích: Đảm bảo hàm trả về chuỗi định dạng đúng (bao gồm tên khóa học và thời gian) trong Youdemi.
     */
    @Test
    @DisplayName("Should return correct format of date")
    void testFormattedOutput() {
        EnrollmentSchedule future = mockSchedule(LocalDate.now().plusDays(1), LocalTime.of(10, 30), "Rust");
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(List.of(future));

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).contains("Rust - ");
    }

    /**
     * TC 5.8: Kiểm tra xử lý an toàn khi danh sách lịch học là null
     * Phân vùng tương đương (Equivalence Partition):
     *   - IP3: Danh sách lịch học là null
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh xử lý schedules == null (trả về "No upcoming session")
     * Mục đích: Đảm bảo hàm xử lý an toàn và trả về "No upcoming session" trong Youdemi khi repository trả về null.
     */
    @Test
    @DisplayName("Should ignore null values safely")
    void testNullHandling() {
        when(enrollmentScheduleRepository.findAllByMenteeId(menteeId)).thenReturn(null);

        String result = dashboardService.getNextSessionTime(menteeId);
        assertThat(result).isEqualTo("No upcoming session");
    }

    private EnrollmentSchedule mockSchedule(LocalDate date, LocalTime time, String courseName) {
        EnrollmentSchedule schedule = mock(EnrollmentSchedule.class);
        Slot slot = mock(Slot.class);
        when(slot.getStartTime()).thenReturn(time);

        Course course = mock(Course.class);
        when(course.getName()).thenReturn(courseName);

        CourseMentor courseMentor = mock(CourseMentor.class);
        when(courseMentor.getCourse()).thenReturn(course);

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getCourseMentor()).thenReturn(courseMentor);

        when(schedule.getSlot()).thenReturn(slot);
        when(schedule.getDate()).thenReturn(date);
        when(schedule.getEnrollment()).thenReturn(enrollment);

        return schedule;
    }
}