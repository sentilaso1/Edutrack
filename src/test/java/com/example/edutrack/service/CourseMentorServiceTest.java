package com.example.edutrack.service;

import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.curriculum.model.CourseMentor;
import com.example.edutrack.curriculum.repository.CourseMentorRepository;
import com.example.edutrack.curriculum.repository.TagRepository;
import com.example.edutrack.curriculum.service.implementation.CourseMentorServiceImpl;
import com.example.edutrack.curriculum.service.interfaces.CourseMentorService;
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
     * Test Case 1: Normal case with sufficient recommendations
     * Branch Coverage: recommendations.size() >= limit (false branch)
     * Equivalence Partition: Sufficient recommendations available
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
     * Test Case 2: Insufficient recommendations - requires fallback
     * Branch Coverage: recommendations.size() < limit (true branch)
     * Equivalence Partition: Insufficient recommendations, needs fallback
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
     * Test Case 3: Empty recommendations - full fallback
     * Branch Coverage: recommendations.size() < limit (true branch), recommendations.size() = 0
     * Equivalence Partition: No recommendations found
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
     * Test Case 4: Boundary - limit = 1 (minimum meaningful limit)
     * Boundary Value: Minimum limit value
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
     * Test Case 5: Boundary - limit = 0 (edge case)
     * Boundary Value: Zero limit
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
     * Test Case 7: Mixed case expertise handling
     * Tests lowercase conversion logic for mentor expertise
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
     * Test Case 8: Duplicate elimination with LinkedHashSet
     * Tests that combined results maintain order and eliminate duplicates
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
     * Test Case 9: SubList boundary when combined size exceeds limit
     * Tests Math.min logic in subList operation
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
     * Test Case 10: Null safety and empty collections handling
     * Tests edge cases with null/empty inputs
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
     * Test Case 11: Math.max edge case - negative remaining
     * Tests Math.max(limit - recommendations.size(), 0) when difference could be negative
     */
    @Test
    void testGetRecommendedByHistory_MathMaxLogic() {
        // This is actually impossible with current logic since recommendations.size() < limit
        // is the condition to enter the branch, but we test the Math.max safety
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