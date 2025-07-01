
package com.example.edutrack.service;

import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.implementation.CourseMentorServiceImpl;
import com.example.edutrack.timetables.service.implementation.EnrollmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// Hàm F1: public List<CourseMentor> getRecommendedByHistory(UUID menteeId, int limit)
// trong CourseMentorServiceImpl
@ExtendWith(MockitoExtension.class)
class CourseMentorServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private CourseMentorRepository courseMentorRepository;

    @Mock
    private EnrollmentServiceImpl enrollmentServiceImpl;

    @InjectMocks
    private CourseMentorServiceImpl courseMentorService;

    private UUID testMenteeId;
    private List<CourseMentor> mockCourseMentors;
    private List<CourseMentor> mockFallbackCourses;

    @BeforeEach
    void setUp() {
        testMenteeId = UUID.randomUUID();

        mockCourseMentors = Arrays.asList(
                createMockCourseMentor(1L, "Java Course"),
                createMockCourseMentor(2L, "Python Course"),
                createMockCourseMentor(3L, "JavaScript Course")
        );

        mockFallbackCourses = Arrays.asList(
                createMockCourseMentor(4L, "Popular Course 1"),
                createMockCourseMentor(5L, "Popular Course 2"),
                createMockCourseMentor(6L, "Popular Course 3")
        );
    }

    private CourseMentor createMockCourseMentor(Long id, String title) {
        CourseMentor courseMentor = new CourseMentor();
        return courseMentor;
    }

    // ============== EQUIVALENCE PARTITIONING TEST CASES ==============

    /**
     * TC 1.1: Kiểm tra trường hợp bình thường với đủ số lượng gợi ý khóa học
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có tagTitles hợp lệ từ mentee
     *   - VP2: Có mentorExpertise hợp lệ từ mentee
     *   - VP3: Số lượng gợi ý (recommendations.size()) >= limit
     *   - VP4: Limit > 0
     *   - VB1: Limit hợp lệ (limit = 3)
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh recommendations.size() >= limit (false branch)
     *   - Nhánh không gọi getPopularCoursesForGuest khi đủ gợi ý
     *   - Nhánh xây dựng danh sách keywords từ tagTitles và mentorExpertise
     * Mục đích: Đảm bảo hàm trả về đúng số lượng khóa học gợi ý (limit=3) dựa trên lịch sử học tập của mentee trong Youdemi, không cần fallback.
     */
    @Test
    void testGetRecommendedByHistory_SufficientRecommendations() {
        int limit = 3;
        List<String> tagTitles = Arrays.asList("Java", "Spring", "Database");
        List<String> mentorExpertise = Arrays.asList("BACKEND", "FRONTEND", "FULLSTACK");

        when(tagRepository.findDistinctTagTitlesFromMenteeId(testMenteeId))
                .thenReturn(tagTitles);
        when(mentorRepository.findExpertiseOfMentorsByMentee(testMenteeId))
                .thenReturn(mentorExpertise);
        when(courseMentorRepository.findRecommendedByKeywords(anyList(), eq(testMenteeId), any(Pageable.class)))
                .thenReturn(mockCourseMentors);

        List<CourseMentor> result = courseMentorService.getRecommendedByHistory(testMenteeId, limit);

        assertEquals(3, result.size());
        assertEquals(mockCourseMentors, result);

        // Verify interactions
        verify(tagRepository, times(1)).findDistinctTagTitlesFromMenteeId(testMenteeId);
        verify(mentorRepository, times(1)).findExpertiseOfMentorsByMentee(testMenteeId);
        verify(courseMentorRepository, times(1)).findRecommendedByKeywords(anyList(), eq(testMenteeId), any(Pageable.class));
        verify(enrollmentServiceImpl, never()).getPopularCoursesForGuest(anyInt());

        // Verify keywords construction
        ArgumentCaptor<List<String>> keywordsCaptor = ArgumentCaptor.forClass(List.class);
        verify(courseMentorRepository).findRecommendedByKeywords(keywordsCaptor.capture(), eq(testMenteeId), any(Pageable.class));
        List<String> capturedKeywords = keywordsCaptor.getValue();
        assertTrue(capturedKeywords.containsAll(Arrays.asList("Java", "Spring", "Database", "backend", "frontend", "fullstack")));
    }

    /**
     * TC 1.2: Kiểm tra trường hợp số lượng gợi ý không đủ, cần fallback
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có tagTitles hợp lệ từ mentee
     *   - VP2: Có mentorExpertise hợp lệ từ mentee
     *   - IP1: Số lượng gợi ý (recommendations.size()) < limit
     *   - VP4: Limit > 0
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh recommendations.size() < limit (true branch)
     *   - Nhánh gọi getPopularCoursesForGuest để bổ sung khóa học phổ biến
     *   - Nhánh kết hợp recommendations và fallbackCourses
     * Mục đích: Đảm bảo hàm bổ sung các khóa học phổ biến từ Youdemi khi số lượng gợi ý dựa trên lịch sử học tập không đủ limit.
     */
    @Test
    void testGetRecommendedByHistory_InsufficientRecommendations_RequiresFallback() {
        // Arrange
        int limit = 5;
        List<String> tagTitles = Arrays.asList("React", "Node.js");
        List<String> mentorExpertise = Arrays.asList("FRONTEND");
        List<CourseMentor> limitedRecommendations = Arrays.asList(mockCourseMentors.get(0), mockCourseMentors.get(1));

        when(tagRepository.findDistinctTagTitlesFromMenteeId(testMenteeId))
                .thenReturn(tagTitles);
        when(mentorRepository.findExpertiseOfMentorsByMentee(testMenteeId))
                .thenReturn(mentorExpertise);
        when(courseMentorRepository.findRecommendedByKeywords(anyList(), eq(testMenteeId), any(Pageable.class)))
                .thenReturn(limitedRecommendations);
        when(enrollmentServiceImpl.getPopularCoursesForGuest(3))
                .thenReturn(mockFallbackCourses);

        // Act
        List<CourseMentor> result = courseMentorService.getRecommendedByHistory(testMenteeId, limit);

        // Assert
        assertEquals(5, result.size());
        assertTrue(result.containsAll(limitedRecommendations));
        assertTrue(result.containsAll(mockFallbackCourses));

        // Verify fallback service was called with correct remaining count
        verify(enrollmentServiceImpl, times(1)).getPopularCoursesForGuest(3);
    }

    /**
     * TC 1.3: Kiểm tra trường hợp không có gợi ý, sử dụng toàn bộ fallback
     * Phân vùng tương đương (Equivalence Partition):
     *   - IP2: Không có tagTitles (rỗng)
     *   - IP3: Không có mentorExpertise (rỗng)
     *   - IP1: Số lượng gợi ý (recommendations.size()) = 0
     *   - VP4: Limit > 0
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh recommendations.size() < limit (true branch)
     *   - Nhánh recommendations.size() = 0
     *   - Nhánh gọi getPopularCoursesForGuest với limit đầy đủ
     * Mục đích: Đảm bảo hàm trả về danh sách khóa học phổ biến từ Youdemi khi không có gợi ý dựa trên lịch sử học tập.
     */
    @Test
    void testGetRecommendedByHistory_EmptyRecommendations_FullFallback() {
        // Arrange
        int limit = 3;
        List<String> tagTitles = Collections.emptyList();
        List<String> mentorExpertise = Collections.emptyList();
        List<CourseMentor> emptyRecommendations = Collections.emptyList();

        when(tagRepository.findDistinctTagTitlesFromMenteeId(testMenteeId))
                .thenReturn(tagTitles);
        when(mentorRepository.findExpertiseOfMentorsByMentee(testMenteeId))
                .thenReturn(mentorExpertise);
        when(courseMentorRepository.findRecommendedByKeywords(anyList(), eq(testMenteeId), any(Pageable.class)))
                .thenReturn(emptyRecommendations);
        when(enrollmentServiceImpl.getPopularCoursesForGuest(3))
                .thenReturn(mockFallbackCourses);

        // Act
        List<CourseMentor> result = courseMentorService.getRecommendedByHistory(testMenteeId, limit);

        // Assert
        assertEquals(3, result.size());
        assertEquals(mockFallbackCourses, result);

        // Verify Math.max(limit - recommendations.size(), 0) logic
        verify(enrollmentServiceImpl, times(1)).getPopularCoursesForGuest(3);
    }

    // ============== BOUNDARY VALUE ANALYSIS TEST CASES ==============

    /**
     * TC 1.4: Kiểm tra giá trị biên với limit = 1 (giá trị tối thiểu có nghĩa)
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có tagTitles hợp lệ từ mentee
     *   - VP2: Có mentorExpertise hợp lệ từ mentee
     *   - VP3: Số lượng gợi ý (recommendations.size()) >= limit
     *   - VB2: Limit = 1 (giá trị biên tối thiểu)
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh recommendations.size() >= limit (false branch)
     *   - Nhánh tạo Pageable với pageSize = 1
     * Mục đích: Đảm bảo hàm trả về đúng 1 khóa học gợi ý trong Youdemi khi limit được đặt ở giá trị biên tối thiểu.
     */
    @Test
    void testGetRecommendedByHistory_BoundaryLimit1() {
        // Arrange
        int limit = 1;
        List<String> tagTitles = Arrays.asList("Java");
        List<String> mentorExpertise = Arrays.asList("BACKEND");

        when(tagRepository.findDistinctTagTitlesFromMenteeId(testMenteeId))
                .thenReturn(tagTitles);
        when(mentorRepository.findExpertiseOfMentorsByMentee(testMenteeId))
                .thenReturn(mentorExpertise);
        when(courseMentorRepository.findRecommendedByKeywords(anyList(), eq(testMenteeId), any(Pageable.class)))
                .thenReturn(Arrays.asList(mockCourseMentors.get(0)));

        // Act
        List<CourseMentor> result = courseMentorService.getRecommendedByHistory(testMenteeId, limit);

        // Assert
        assertEquals(1, result.size());

        // Verify PageRequest creation with limit
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(courseMentorRepository).findRecommendedByKeywords(anyList(), eq(testMenteeId), pageableCaptor.capture());
        assertEquals(1, pageableCaptor.getValue().getPageSize());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
    }

    /**
     * TC 1.5: Kiểm tra giá trị biên với limit = 0 (trường hợp biên không hợp lệ)
     * Phân vùng tương đương (Equivalence Partition):
     *   - IB1: Limit = 0
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh kiểm tra limit <= 0 (trả về danh sách rỗng)
     * Mục đích: Đảm bảo hàm trả về danh sách rỗng và không gọi bất kỳ repository nào trong Youdemi khi limit bằng 0.
     */
    @Test
    void testGetRecommendedByHistory_BoundaryLimit0() {
        int limit = 0;

        List<CourseMentor> result = courseMentorService.getRecommendedByHistory(testMenteeId, limit);

        assertEquals(0, result.size());

        verifyNoInteractions(tagRepository);
        verifyNoInteractions(mentorRepository);
        verifyNoInteractions(courseMentorRepository);
        verifyNoInteractions(enrollmentServiceImpl);
    }

    // ============== COMPLEX SCENARIOS & EDGE CASES ==============

    /**
     * TC 1.7: Kiểm tra xử lý mentor expertise với chữ hoa/thường lẫn lộn
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có tagTitles hợp lệ từ mentee
     *   - VP2: Có mentorExpertise hợp lệ từ mentee
     *   - VP3: Số lượng gợi ý (recommendations.size()) >= limit
     *   - VP4: Limit > 0
     *   - VP5: Mentor expertise chứa chữ hoa/thường lẫn lộn
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh chuyển đổi mentorExpertise sang chữ thường
     *   - Nhánh xây dựng danh sách keywords từ tagTitles và mentorExpertise
     * Mục đích: Đảm bảo hàm xử lý đúng các expertise của mentor (chữ hoa/thường) trong Youdemi để tạo danh sách keyword chính xác.
     */
    @Test
    void testGetRecommendedByHistory_MixedCaseExpertise() {
        // Arrange
        int limit = 3;
        List<String> tagTitles = Arrays.asList("React");
        List<String> mentorExpertise = Arrays.asList("FRONTEND", "Backend", "FullStack", "mobile");

        when(tagRepository.findDistinctTagTitlesFromMenteeId(testMenteeId))
                .thenReturn(tagTitles);
        when(mentorRepository.findExpertiseOfMentorsByMentee(testMenteeId))
                .thenReturn(mentorExpertise);
        when(courseMentorRepository.findRecommendedByKeywords(anyList(), eq(testMenteeId), any(Pageable.class)))
                .thenReturn(mockCourseMentors);

        // Act
        courseMentorService.getRecommendedByHistory(testMenteeId, limit);

        // Assert - verify lowercase conversion
        ArgumentCaptor<List<String>> keywordsCaptor = ArgumentCaptor.forClass(List.class);
        verify(courseMentorRepository).findRecommendedByKeywords(keywordsCaptor.capture(), eq(testMenteeId), any(Pageable.class));
        List<String> capturedKeywords = keywordsCaptor.getValue();
        assertTrue(capturedKeywords.contains("frontend"));
        assertTrue(capturedKeywords.contains("backend"));
        assertTrue(capturedKeywords.contains("fullstack"));
        assertTrue(capturedKeywords.contains("mobile"));
        assertTrue(capturedKeywords.contains("React"));
    }

    /**
     * TC 1.8: Kiểm tra loại bỏ trùng lặp khi kết hợp gợi ý và fallback
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có tagTitles hợp lệ từ mentee
     *   - VP2: Có mentorExpertise hợp lệ từ mentee
     *   - IP1: Số lượng gợi ý (recommendations.size()) < limit
     *   - VP4: Limit > 0
     *   - VP6: Có khóa học trùng lặp giữa recommendations và fallbackCourses
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh recommendations.size() < limit (true branch)
     *   - Nhánh sử dụng LinkedHashSet để loại bỏ trùng lặp
     *   - Nhánh duy trì thứ tự khi kết hợp recommendations và fallbackCourses
     * Mục đích: Đảm bảo hàm loại bỏ các khóa học trùng lặp trong Youdemi và giữ đúng thứ tự ưu tiên (recommendations trước, fallback sau).
     */
    @Test
    void testGetRecommendedByHistory_DuplicateElimination() {
        // Arrange
        int limit = 4;
        List<String> tagTitles = Arrays.asList("Java");
        List<String> mentorExpertise = Arrays.asList("BACKEND");

        // Create scenario where fallback might contain duplicates
        List<CourseMentor> partialRecommendations = Arrays.asList(mockCourseMentors.get(0));
        List<CourseMentor> fallbackWithDuplicate = Arrays.asList(
                mockCourseMentors.get(0), // Duplicate
                mockFallbackCourses.get(0),
                mockFallbackCourses.get(1)
        );

        when(tagRepository.findDistinctTagTitlesFromMenteeId(testMenteeId))
                .thenReturn(tagTitles);
        when(mentorRepository.findExpertiseOfMentorsByMentee(testMenteeId))
                .thenReturn(mentorExpertise);
        when(courseMentorRepository.findRecommendedByKeywords(anyList(), eq(testMenteeId), any(Pageable.class)))
                .thenReturn(partialRecommendations);
        when(enrollmentServiceImpl.getPopularCoursesForGuest(3))
                .thenReturn(fallbackWithDuplicate);

        // Act
        List<CourseMentor> result = courseMentorService.getRecommendedByHistory(testMenteeId, limit);

        // Assert - verify duplicates are eliminated and order is maintained
        assertEquals(3, result.size()); // Should be 3, not 4, due to duplicate elimination
        assertEquals(mockCourseMentors.get(0), result.get(0)); // Original recommendation comes first
    }

    /**
     * TC 1.9: Kiểm tra giá trị biên khi tổng số khóa học vượt quá limit
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có tagTitles hợp lệ từ mentee
     *   - VP2: Có mentorExpertise hợp lệ từ mentee
     *   - IP1: Số lượng gợi ý (recommendations.size()) < limit
     *   - VP4: Limit > 0
     *   - VP7: Tổng số khóa học (recommendations + fallback) > limit
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh recommendations.size() < limit (true branch)
     *   - Nhánh sử dụng Math.min để giới hạn số lượng khóa học trả về
     * Mục đích: Đảm bảo hàm chỉ trả về đúng số lượng khóa học theo limit trong Youdemi khi tổng số khóa học vượt quá giới hạn.
     */
    @Test
    void testGetRecommendedByHistory_SubListBoundary() {
        // Arrange
        int limit = 3;
        List<String> tagTitles = Arrays.asList("Vue");
        List<String> mentorExpertise = Arrays.asList("FRONTEND");
        List<CourseMentor> partialRecommendations = Arrays.asList(mockCourseMentors.get(0));

        when(tagRepository.findDistinctTagTitlesFromMenteeId(testMenteeId))
                .thenReturn(tagTitles);
        when(mentorRepository.findExpertiseOfMentorsByMentee(testMenteeId))
                .thenReturn(mentorExpertise);
        when(courseMentorRepository.findRecommendedByKeywords(anyList(), eq(testMenteeId), any(Pageable.class)))
                .thenReturn(partialRecommendations);
        when(enrollmentServiceImpl.getPopularCoursesForGuest(2))
                .thenReturn(mockFallbackCourses); // Returns 3 items, but we only want 2 more

        // Act
        List<CourseMentor> result = courseMentorService.getRecommendedByHistory(testMenteeId, limit);

        // Assert - should be limited to exactly 'limit' items
        assertEquals(3, result.size());
    }

    /**
     * TC 1.10: Kiểm tra xử lý an toàn với danh sách đầu vào rỗng
     * Phân vùng tương đương (Equivalence Partition):
     *   - IP2: Không có tagTitles (rỗng)
     *   - IP3: Không có mentorExpertise (rỗng)
     *   - IP1: Số lượng gợi ý (recommendations.size()) = 0
     *   - VP4: Limit > 0
     *   - IP7: FallbackCourses nhỏ hơn limit
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh recommendations.size() < limit (true branch)
     *   - Nhánh xử lý danh sách keywords rỗng
     *   - Nhánh gọi getPopularCoursesForGuest khi không có gợi ý
     * Mục đích: Đảm bảo hàm xử lý an toàn khi không có tagTitles hoặc mentorExpertise, trả về fallbackCourses trong Youdemi.
     */
    @Test
    void testGetRecommendedByHistory_EmptyInputs() {
        // Arrange
        int limit = 5;
        List<String> emptyTagTitles = Collections.emptyList();
        List<String> emptyMentorExpertise = Collections.emptyList();
        List<CourseMentor> emptyRecommendations = Collections.emptyList();

        when(tagRepository.findDistinctTagTitlesFromMenteeId(testMenteeId))
                .thenReturn(emptyTagTitles);
        when(mentorRepository.findExpertiseOfMentorsByMentee(testMenteeId))
                .thenReturn(emptyMentorExpertise);
        when(courseMentorRepository.findRecommendedByKeywords(anyList(), eq(testMenteeId), any(Pageable.class)))
                .thenReturn(emptyRecommendations);
        when(enrollmentServiceImpl.getPopularCoursesForGuest(5))
                .thenReturn(mockFallbackCourses.subList(0, 3)); // Less than requested

        // Act
        List<CourseMentor> result = courseMentorService.getRecommendedByHistory(testMenteeId, limit);

        // Assert
        assertEquals(3, result.size());

        // Verify empty keywords are handled properly
        ArgumentCaptor<List<String>> keywordsCaptor = ArgumentCaptor.forClass(List.class);
        verify(courseMentorRepository).findRecommendedByKeywords(keywordsCaptor.capture(), eq(testMenteeId), any(Pageable.class));
        assertTrue(keywordsCaptor.getValue().isEmpty());
    }

    // ============== ADDITIONAL BRANCH COVERAGE TESTS ==============

    /**
     * TC 1.11: Kiểm tra logic Math.max với số lượng khóa học còn lại âm
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Có tagTitles hợp lệ từ mentee
     *   - VP2: Có mentorExpertise hợp lệ từ mentee
     *   - IP1: Số lượng gợi ý (recommendations.size()) < limit
     *   - VP4: Limit > 0
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh recommendations.size() < limit (true branch)
     *   - Nhánh Math.max(limit - recommendations.size(), 0)
     * Mục đích: Đảm bảo hàm tính đúng số lượng khóa học cần lấy từ fallback trong Youdemi khi số lượng còn lại không âm.
     */
    @Test
    void testGetRecommendedByHistory_MathMaxLogic() {
        int limit = 3;
        List<String> tagTitles = Arrays.asList("Angular");
        List<String> mentorExpertise = Arrays.asList("FRONTEND");
        List<CourseMentor> partialRecommendations = Arrays.asList(mockCourseMentors.get(0));

        when(tagRepository.findDistinctTagTitlesFromMenteeId(testMenteeId))
                .thenReturn(tagTitles);
        when(mentorRepository.findExpertiseOfMentorsByMentee(testMenteeId))
                .thenReturn(mentorExpertise);
        when(courseMentorRepository.findRecommendedByKeywords(anyList(), eq(testMenteeId), any(Pageable.class)))
                .thenReturn(partialRecommendations);
        when(enrollmentServiceImpl.getPopularCoursesForGuest(2))
                .thenReturn(mockFallbackCourses.subList(0, 2));

        // Act
        List<CourseMentor> result = courseMentorService.getRecommendedByHistory(testMenteeId, limit);

        // Assert
        assertEquals(3, result.size());
        verify(enrollmentServiceImpl, times(1)).getPopularCoursesForGuest(2);
    }
}
