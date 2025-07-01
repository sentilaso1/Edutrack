package com.example.edutrack.service;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.dto.SkillProgressDTO;
import com.example.edutrack.curriculum.model.Course;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.model.Tag;
import com.example.edutrack.curriculum.repository.GoalRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.implementation.DashboardServiceImpl;
import com.example.edutrack.timetables.model.Enrollment;
import com.example.edutrack.timetables.model.EnrollmentSchedule;
import com.example.edutrack.timetables.repository.EnrollmentRepository;
import com.example.edutrack.timetables.repository.EnrollmentScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


// Hàm F2: public List<SkillProgressDTO> getSkillProgressList(UUID menteeId, String keyword, YearMonth selectedMonth, UUID mentorId)
// trong DashboardServiceImpl
@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardServiceImpl - getSkillProgressList Tests")
class DashboardServiceImplTest {

    @Mock
    private EnrollmentScheduleRepository enrollmentScheduleRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private UUID menteeId;
    private UUID courseId1;
    private UUID courseId2;
    private UUID mentorId1;
    private UUID mentorId2;
    private Enrollment enrollment1;
    private Enrollment enrollment2;
    private Enrollment enrollment3;

    @BeforeEach
    void setUp() {
        menteeId = UUID.randomUUID();
        courseId1 = UUID.randomUUID();
        courseId2 = UUID.randomUUID();
        mentorId1 = UUID.randomUUID();
        mentorId2 = UUID.randomUUID();

        // Setup test data
        setupTestEnrollments();
    }

    private void setupTestEnrollments() {
        // Course 1 - Java Programming
        Course course1 = new Course();
        course1.setId(courseId1);
        course1.setName("Java Programming");

        Mentor mentor1 = new Mentor();
        mentor1.setId(mentorId1);
        mentor1.setFullName("John Doe");

        CourseMentor courseMentor1 = new CourseMentor();
        courseMentor1.setCourse(course1);
        courseMentor1.setMentor(mentor1);

        enrollment1 = new Enrollment();
        enrollment1.setId(1L);
        enrollment1.setCourseMentor(courseMentor1);
        enrollment1.setTotalSlots(10);
        enrollment1.setStartTime("2024-01-15 10:00:00.000000");

        // Course 2 - Python Basics
        Course course2 = new Course();
        course2.setId(courseId2);
        course2.setName("Python Basics");

        Mentor mentor2 = new Mentor();
        mentor2.setId(mentorId2);
        mentor2.setFullName("Jane Smith");

        CourseMentor courseMentor2 = new CourseMentor();
        courseMentor2.setCourse(course2);
        courseMentor2.setMentor(mentor2);

        enrollment2 = new Enrollment();
        enrollment2.setId(2L);
        enrollment2.setCourseMentor(courseMentor2);
        enrollment2.setTotalSlots(8);
        enrollment2.setStartTime("2024-02-10 14:00:00.000000");

        // Course 3 - Invalid date format
        Course course3 = new Course();
        course3.setId(UUID.randomUUID());
        course3.setName("Database Design");

        CourseMentor courseMentor3 = new CourseMentor();
        courseMentor3.setCourse(course3);
        courseMentor3.setMentor(mentor1);

        enrollment3 = new Enrollment();
        enrollment3.setId(3L);
        enrollment3.setCourseMentor(courseMentor3);
        enrollment3.setTotalSlots(5);
        enrollment3.setStartTime("invalid-date-format");
    }

    /**
     * TC 2.1: Kiểm tra trường hợp không áp dụng bộ lọc, trả về tất cả các khóa học
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có nhiều enrollment hợp lệ
     *   - VP2: Total slots > 0
     *   - VP3: Có sessions được tham gia
     *   - VP4: Total slots hợp lệ
     *   - VP5: Có mentor hợp lệ
     *   - VP6: Mentor không null
     *   - VB2: Null mentorId filter
     *   - VB3: Null month filter
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh không áp dụng bộ lọc keyword, mentorId, month (keyword == null, mentorId == null, month == null)
     *   - Nhánh tính toán tiến độ (progress = sessionsCompleted / totalSlots * 100)
     *   - Nhánh xử lý danh sách tags từ TagRepository
     * Mục đích: Đảm bảo hàm trả về danh sách tất cả các khóa học với thông tin đúng (tiêu đề, tiến độ, mentor, tags) khi không có bộ lọc.
     */
    @Test
    @DisplayName("Should return all skills when no filters applied")
    void testGetSkillProgressList_NoFilters_ReturnsAllSkills() {
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(enrollments);

        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(1L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(7);
        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(2L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(5);

        when(enrollmentScheduleRepository.findLastPresentSessionDate(1L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(LocalDate.of(2024, 1, 20));
        when(enrollmentScheduleRepository.findLastPresentSessionDate(2L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(LocalDate.of(2024, 2, 15));

        when(tagRepository.findByCourseId(courseId1))
                .thenReturn(Arrays.asList(createTag("Programming"), createTag("Java")));
        when(tagRepository.findByCourseId(courseId2))
                .thenReturn(Arrays.asList(createTag("Programming"), createTag("Python")));

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, null, null, null);

        assertThat(result).hasSize(2);

        SkillProgressDTO skill1 = result.get(0);
        assertThat(skill1.getCourseTitle()).isEqualTo("Java Programming");
        assertThat(skill1.getSessionsCompleted()).isEqualTo(7);
        assertThat(skill1.getProgressPercentage()).isEqualTo(70);
        assertThat(skill1.getTotalSessions()).isEqualTo(10);
        assertThat(skill1.getMentorName()).isEqualTo("John Doe");
        assertThat(skill1.getTags()).containsExactly("Programming", "Java");

        SkillProgressDTO skill2 = result.get(1);
        assertThat(skill2.getCourseTitle()).isEqualTo("Python Basics");
        assertThat(skill2.getSessionsCompleted()).isEqualTo(5);
        assertThat(skill2.getProgressPercentage()).isEqualTo(63);
        assertThat(skill2.getTotalSessions()).isEqualTo(8);
        assertThat(skill2.getMentorName()).isEqualTo("Jane Smith");
    }

    /**
     * TC 2.2: Kiểm tra lọc theo keyword với tìm kiếm không phân biệt hoa thường
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có enrollment hợp lệ
     *   - VP2: Total slots > 0
     *   - VP3: Có sessions được tham gia
     *   - VP4: Total slots hợp lệ
     *   - VP5: Có mentor hợp lệ
     *   - VP6: Mentor không null
     *   - VB1: Keyword tìm kiếm không phân biệt hoa thường
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh áp dụng bộ lọc keyword (keyword != null && !keyword.trim().isEmpty())
     *   - Nhánh kiểm tra courseName.contains(keyword, ignoreCase)
     * Mục đích: Đảm bảo hàm lọc đúng khóa học dựa trên keyword không phân biệt hoa thường (VD: "JAVA" khớp với "Java Programming").
     */
    @Test
    @DisplayName("Should filter by keyword - case insensitive")
    void testGetSkillProgressList_KeywordFilter_CaseInsensitive() {
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(enrollments);

        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(1L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(7);
        when(enrollmentScheduleRepository.findLastPresentSessionDate(1L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(LocalDate.of(2024, 1, 20));
        when(tagRepository.findByCourseId(courseId1))
                .thenReturn(Arrays.asList(createTag("Programming")));

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, "JAVA", null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCourseTitle()).isEqualTo("Java Programming");
    }

    /**
     * TC 2.3: Kiểm tra lọc theo keyword không có kết quả khớp
     * Phân vùng tương đương (Equivalence Partition):
     *   - IP1: Keyword không khớp với bất kỳ khóa học nào
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh áp dụng bộ lọc keyword (keyword != null && !keyword.trim().isEmpty())
     *   - Nhánh không tìm thấy khóa học khớp với keyword
     * Mục đích: Đảm bảo hàm trả về danh sách rỗng khi keyword không khớp với bất kỳ khóa học nào.
     */
    @Test
    @DisplayName("Should filter by keyword - no matches")
    void testGetSkillProgressList_KeywordFilter_NoMatches() {
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(enrollments);

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, "NonExistent", null, null);

        assertThat(result).isEmpty();
    }

    /**
     * TC 2.4: Kiểm tra bỏ qua bộ lọc khi keyword rỗng hoặc chỉ chứa khoảng trắng
     * Phân vùng tương đương (Equivalence Partition):
     *   - IB1: Keyword rỗng hoặc chỉ chứa khoảng trắng
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh bỏ qua bộ lọc keyword (keyword == null || keyword.trim().isEmpty())
     * Mục đích: Đảm bảo hàm bỏ qua bộ lọc keyword và trả về tất cả các khóa học khi keyword là rỗng hoặc chỉ chứa khoảng trắng.
     */
    @Test
    @DisplayName("Should filter by keyword - blank keyword should be ignored")
    void testGetSkillProgressList_BlankKeyword_ShouldIgnoreFilter() {
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(enrollments);

        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(any(), any()))
                .thenReturn(5);
        when(enrollmentScheduleRepository.findLastPresentSessionDate(any(), any()))
                .thenReturn(LocalDate.of(2024, 1, 15));
        when(tagRepository.findByCourseId(any()))
                .thenReturn(Collections.emptyList());

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, "   ", null, null);

        assertThat(result).hasSize(2);
    }

    /**
     * TC 2.5: Kiểm tra lọc theo mentor ID
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP2: Mentor ID khớp với enrollment
     *   - VP1: Có enrollment hợp lệ
     *   - VP3: Có sessions được tham gia
     *   - VP4: Total slots hợp lệ
     *   - VP5: Có mentor hợp lệ
     *   - VP6: Mentor không null
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh áp dụng bộ lọc mentorId (mentorId != null)
     *   - Nhánh kiểm tra mentor.getId().equals(mentorId)
     * Mục đích: Đảm bảo hàm chỉ trả về các khóa học của mentor được chỉ định.
     */
    @Test
    @DisplayName("Should filter by mentor ID")
    void testGetSkillProgressList_MentorFilter() {
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(enrollments);

        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(1L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(7);
        when(enrollmentScheduleRepository.findLastPresentSessionDate(1L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(LocalDate.of(2024, 1, 20));
        when(tagRepository.findByCourseId(courseId1))
                .thenReturn(Arrays.asList(createTag("Java")));

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, null, null, mentorId1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCourseTitle()).isEqualTo("Java Programming");
        assertThat(result.get(0).getMentorName()).isEqualTo("John Doe");
    }

    /**
     * TC 2.6: Kiểm tra lọc theo tháng bắt đầu khóa học
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP3: Tháng bắt đầu khớp với enrollment
     *   - VP1: Có enrollment hợp lệ
     *   - VP2: Total slots > 0
     *   - VP4: Total slots hợp lệ
     *   - VP5: Có mentor hợp lệ
     *   - VP6: Mentor không null
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh áp dụng bộ lọc month (month != null)
     *   - Nhánh kiểm tra startTime khớp với YearMonth
     * Mục đích: Đảm bảo hàm chỉ trả về các khóa học có tháng bắt đầu khớp với bộ lọc.
     */
    @Test
    @DisplayName("Should filter by month from startTime")
    void testGetSkillProgressList_MonthFilter() {
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(enrollments);

        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(2L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(5);
        when(enrollmentScheduleRepository.findLastPresentSessionDate(2L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(LocalDate.of(2024, 2, 15));
        when(tagRepository.findByCourseId(courseId2))
                .thenReturn(Arrays.asList(createTag("Python")));

        YearMonth february2024 = YearMonth.of(2024, 2);
        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, null, february2024, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCourseTitle()).isEqualTo("Python Basics");
    }

    /**
     * TC 2.7: Kiểm tra xử lý định dạng ngày không hợp lệ
     * Phân vùng tương đương (Equivalence Partition):
     *   - IB2: Định dạng ngày không hợp lệ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh xử lý ngoại lệ khi parse startTime thất bại
     *   - Nhánh bỏ qua enrollment với startTime không hợp lệ
     * Mục đích: Đảm bảo hàm bỏ qua các enrollment có định dạng ngày không hợp lệ và trả về danh sách rỗng.
     */
    @Test
    @DisplayName("Should handle invalid date format gracefully")
    void testGetSkillProgressList_InvalidDateFormat_ShouldSkip() {
        List<Enrollment> enrollments = Arrays.asList(enrollment3);

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(enrollments);

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, null, YearMonth.of(2024, 1), null);

        assertThat(result).isEmpty();
    }

    /**
     * TC 2.8: Kiểm tra xử lý mentor null
     * Phân vùng tương đương (Equivalence Partition):
     *   - IP6: Mentor null
     *   - VP1: Có enrollment hợp lệ
     *   - VP2: Total slots > 0
     *   - VP3: Có sessions được tham gia
     *   - VP4: Total slots hợp lệ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh xử lý mentor null (courseMentor.getMentor() == null)
     *   - Nhánh gán mentorName = "Unknown" khi mentor null
     * Mục đích: Đảm bảo hàm hiển thị "Unknown" cho mentorName khi mentor là null.
     */
    @Test
    @DisplayName("Should handle null mentor gracefully")
    void testGetSkillProgressList_NullMentor_ShouldUseUnknown() {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setName("Test Course");

        CourseMentor courseMentor = new CourseMentor();
        courseMentor.setCourse(course);
        courseMentor.setMentor(null);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(4L);
        enrollment.setCourseMentor(courseMentor);
        enrollment.setTotalSlots(5);
        enrollment.setStartTime("2024-01-15 10:00:00.000000");

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(Arrays.asList(enrollment));

        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(4L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(3);
        when(enrollmentScheduleRepository.findLastPresentSessionDate(4L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(LocalDate.of(2024, 1, 20));
        when(tagRepository.findByCourseId(any()))
                .thenReturn(Collections.emptyList());

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMentorName()).isEqualTo("Unknown");
        assertThat(result.get(0).getMentorId()).isNull();
    }

    /**
     * TC 2.9: Kiểm tra xử lý totalSlots null
     * Phân vùng tương đương (Equivalence Partition):
     *   - IP4: Total slots null
     *   - VP1: Có enrollment hợp lệ
     *   - VP3: Có sessions được tham gia
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh xử lý totalSlots == null (gán default totalSlots = 1)
     *   - Nhánh tính toán tiến độ khi totalSlots được gán mặc định
     * Mục đích: Đảm bảo hàm gán totalSlots = 1 khi null và tính tiến độ đúng (100% nếu có sessions được tham gia).
     */
    @Test
    @DisplayName("Should handle zero total slots correctly")
    void testGetSkillProgressList_ZeroTotalSlots_ShouldUseDefaultOne() {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setName("Test Course");

        CourseMentor courseMentor = new CourseMentor();
        courseMentor.setCourse(course);
        courseMentor.setMentor(null);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(5L);
        enrollment.setCourseMentor(courseMentor);
        enrollment.setTotalSlots(null);
        enrollment.setStartTime("2024-01-15 10:00:00.000000");

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(Arrays.asList(enrollment));

        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(5L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(1);
        when(enrollmentScheduleRepository.findLastPresentSessionDate(5L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(LocalDate.of(2024, 1, 20));
        when(tagRepository.findByCourseId(any()))
                .thenReturn(Collections.emptyList());

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotalSessions()).isEqualTo(1);
        assertThat(result.get(0).getProgressPercentage()).isEqualTo(100);
    }

    /**
     * TC 2.10: Kiểm tra kết hợp nhiều bộ lọc (keyword, month, mentorId) không có kết quả khớp
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có enrollment hợp lệ
     *   - VP3: Tháng bắt đầu khớp
     *   - IP2: Mentor ID không khớp
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh áp dụng đồng thời nhiều bộ lọc (keyword != null, month != null, mentorId != null)
     *   - Nhánh không tìm thấy kết quả khi tất cả bộ lọc không khớp
     * Mục đích: Đảm bảo hàm trả về danh sách rỗng khi các bộ lọc kết hợp không tìm thấy enrollment phù hợp.
     */
    @Test
    @DisplayName("Should combine multiple filters correctly")
    void testGetSkillProgressList_MultipleFilters_ShouldApplyAll() {
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(enrollments);

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(
                menteeId, "Java", YearMonth.of(2024, 1), mentorId2);

        assertThat(result).isEmpty();
    }

    /**
     * TC 2.11: Kiểm tra trường hợp không có enrollment nào
     * Phân vùng tương đương (Equivalence Partition):
     *   - IP3: Không có enrollment
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh trả về danh sách rỗng khi không có enrollment (enrollments.isEmpty())
     * Mục đích: Đảm bảo hàm trả về danh sách rỗng khi không tìm thấy enrollment nào.
     */
    @Test
    @DisplayName("Should return empty list when no enrollments found")
    void testGetSkillProgressList_NoEnrollments_ShouldReturnEmpty() {
        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(Collections.emptyList());

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, null, null, null);

        assertThat(result).isEmpty();
    }

    /**
     * TC 2.12: Kiểm tra trường hợp totalSlots = 0
     * Phân vùng tương đương (Equivalence Partition):
     *   - VB4: Total slots = 0
     *   - VP1: Có enrollment hợp lệ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh xử lý totalSlots == 0 (trả về progressPercentage = 0)
     * Mục đích: Đảm bảo hàm trả về tiến độ 0% khi totalSlots bằng 0, bất kể số sessions được tham gia.
     */
    @Test
    @DisplayName("Should handle totalSlots = 0 by returning 0% progress")
    void testGetSkillProgressList_TotalSlotsZero() {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setName("Zero Slot Course");

        CourseMentor courseMentor = new CourseMentor();
        courseMentor.setCourse(course);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(6L);
        enrollment.setCourseMentor(courseMentor);
        enrollment.setTotalSlots(0);
        enrollment.setStartTime("2024-01-15 10:00:00.000000");

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(Arrays.asList(enrollment));
        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(6L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(3);

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProgressPercentage()).isEqualTo(0);
    }

    /**
     * TC 2.13: Kiểm tra trường hợp không có sessions nào được tham gia
     * Phân vùng tương đương (Equivalence Partition):
     *   - IP5: Không có sessions được tham gia
     *   - VP2: Total slots > 0
     *   - VP1: Có enrollment hợp lệ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh xử lý sessionsCompleted == 0 (trả về progressPercentage = 0)
     * Mục đích: Đảm bảo hàm trả về tiến độ 0% khi không có sessions nào được tham gia, với totalSlots > 0.
     */
    @Test
    @DisplayName("Should handle no sessions attended with total slots > 0")
    void testGetSkillProgressList_NoSessionsAttended() {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setName("Test Course");

        CourseMentor courseMentor = new CourseMentor();
        courseMentor.setCourse(course);
        courseMentor.setMentor(null);

        Enrollment enrollment = new Enrollment();
        enrollment.setId(7L);
        enrollment.setCourseMentor(courseMentor);
        enrollment.setTotalSlots(5);
        enrollment.setStartTime("2024-01-15 10:00:00.000000");

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(Arrays.asList(enrollment));

        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(7L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(0);
        when(enrollmentScheduleRepository.findLastPresentSessionDate(7L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(null);
        when(tagRepository.findByCourseId(any()))
                .thenReturn(Collections.emptyList());

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, null, null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotalSessions()).isEqualTo(5);
        assertThat(result.get(0).getSessionsCompleted()).isEqualTo(0);
        assertThat(result.get(0).getProgressPercentage()).isEqualTo(0);
    }

    /**
     * TC 2.14: Kiểm tra trường hợp bộ lọc month là null
     * Phân vùng tương đương (Equivalence Partition):
     *   - VB3: Month filter null
     *   - VP1: Có enrollment hợp lệ
     *   - VP2: Total slots > 0
     *   - VP3: Có sessions được tham gia
     *   - VP4: Total slots hợp lệ
     *   - VP5: Có mentor hợp lệ
     *   - VP6: Mentor không null
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh bỏ qua bộ lọc month (month == null)
     * Mục đích: Đảm bảo hàm trả về tất cả các khóa học khi không áp dụng bộ lọc tháng.
     */
    @Test
    @DisplayName("Should return all enrollments with null month filter")
    void testGetSkillProgressList_NullMonthFilter() {
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(enrollments);

        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(1L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(7);
        when(enrollmentScheduleRepository.countByEnrollment_IdAndAttendance(2L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(5);

        when(enrollmentScheduleRepository.findLastPresentSessionDate(1L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(LocalDate.of(2024, 1, 20));
        when(enrollmentScheduleRepository.findLastPresentSessionDate(2L, EnrollmentSchedule.Attendance.PRESENT))
                .thenReturn(LocalDate.of(2024, 2, 15));

        when(tagRepository.findByCourseId(courseId1))
                .thenReturn(Arrays.asList(createTag("Programming"), createTag("Java")));
        when(tagRepository.findByCourseId(courseId2))
                .thenReturn(Arrays.asList(createTag("Programming"), createTag("Python")));

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(menteeId, null, null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCourseTitle()).isEqualTo("Java Programming");
        assertThat(result.get(1).getCourseTitle()).isEqualTo("Python Basics");
    }

    /**
     * TC 2.15: Kiểm tra kết hợp nhiều bộ lọc với kết quả không khớp
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có enrollment hợp lệ
     *   - VP3: Tháng bắt đầu khớp
     *   - IP2: Mentor ID không khớp
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh áp dụng đồng thời nhiều bộ lọc (keyword != null, month != null, mentorId != null)
     *   - Nhánh không tìm thấy kết quả khi tất cả bộ lọc không khớp
     * Mục đích: Đảm bảo hàm trả về danh sách rỗng khi các bộ lọc kết hợp không tìm thấy enrollment phù hợp.
     */
    @Test
    @DisplayName("Should handle multiple filters with mismatch")
    void testGetSkillProgressList_MultipleFilters_Mismatch() {
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findAcceptedEnrollmentsByMenteeId(menteeId, Enrollment.EnrollmentStatus.APPROVED))
                .thenReturn(enrollments);

        List<SkillProgressDTO> result = dashboardService.getSkillProgressList(
                menteeId, "Java", YearMonth.of(2024, 2), mentorId2);

        assertThat(result).isEmpty();
    }

    private Tag createTag(String title) {
        Tag tag = new Tag();
        tag.setTitle(title);
        return tag;
    }
}