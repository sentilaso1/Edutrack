package com.example.edutrack.controller;

import com.example.edutrack.curriculum.controller.MentorController;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorControllerTest {

    @Mock
    private MentorService mentorService;

    @Mock
    private Model model;

    @InjectMocks
    private MentorController mentorController;

    private List<Mentor> dummyMentors;

    @BeforeEach
    void setup() {
        dummyMentors = List.of(
                createMentor("Alice", List.of("java", "spring")),
                createMentor("Bob", List.of("python", "ml"))
        );
    }

    @Test
    void shouldRedirectTo404_whenPageIsLessThan1() {
        String view = mentorController.viewMentorList(null, new String[]{}, null, null, null, null, 0, 6, null, model);
        assertEquals("redirect:/404", view);
    }

    @Test
    void shouldRedirectToError_whenMentorPageIsNull() {
        when(mentorService.searchMentorsWithApprovedCV(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(null);

        String view = mentorController.viewMentorList(null, new String[]{}, null, null, null, null, 1, 6, null, model);
        assertEquals("redirect:/error", view);
    }

    @ParameterizedTest
    @CsvSource({
            "newest,createdDate,DESC",
            "oldest,createdDate,ASC",
            "name_asc,fullName,ASC",
            "name_desc,fullName,DESC",
            "rating_desc,rating,DESC",
            "rating_asc,rating,ASC",
            "invalid,,UNSORTED",
            ",,,"
    })
    void shouldApplyOrderBy_whenOrderByProvided(String orderBy, String expectedField, String direction) {
        Page<Mentor> page = new PageImpl<>(dummyMentors);
        when(mentorService.searchMentorsWithApprovedCV(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);
        when(mentorService.getAllMentorExpertiseFromApprovedCVs()).thenReturn(List.of("java", "python"));

        String view = mentorController.viewMentorList(null, new String[]{}, null, null, null, null, 1, 6, orderBy, model);
        assertEquals("mentee/mentor-list", view);
    }

    @Test
    void shouldFilterMentorsByExpertise_whenExpertiseProvided() {
        String[] expertise = {"Java"};
        Page<Mentor> filtered = new PageImpl<>(List.of(dummyMentors.get(0)));

        when(mentorService.searchMentorsWithApprovedCV(eq("test"), eq(expertise), any(), any(), any(), any(Pageable.class)))
                .thenReturn(filtered);
        when(mentorService.getAllMentorExpertiseFromApprovedCVs()).thenReturn(List.of("java"));

        String view = mentorController.viewMentorList("test", expertise, null, null, null, null, 1, 6, null, model);
        assertEquals("mentee/mentor-list", view);
    }

    @Test
    void shouldReturnEmptyList_whenExpertiseDoesNotMatch() {
        String[] expertise = {"Golang"};
        Page<Mentor> emptyPage = new PageImpl<>(new ArrayList<>());

        when(mentorService.searchMentorsWithApprovedCV(any(), eq(expertise), any(), any(), any(), any(Pageable.class)))
                .thenReturn(emptyPage);
        when(mentorService.getAllMentorExpertiseFromApprovedCVs()).thenReturn(List.of());

        String view = mentorController.viewMentorList("test", expertise, null, null, null, null, 1, 6, null, model);
        assertEquals("mentee/mentor-list", view);
    }

    @Test
    void shouldAddAllModelAttributesCorrectly() {
        Page<Mentor> page = new PageImpl<>(dummyMentors);
        List<String> expertise = List.of("java", "spring");

        when(mentorService.searchMentorsWithApprovedCV(any(), any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);
        when(mentorService.getAllMentorExpertiseFromApprovedCVs()).thenReturn(expertise);

        String result = mentorController.viewMentorList(
                "alice",
                expertise.toArray(new String[0]),
                4.5,
                null,
                true,
                expertise,
                1,
                6,
                "newest",
                model);

        assertEquals("mentee/mentor-list", result);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100})
    void shouldHandleVariousPageSizes(int size) {
        when(mentorService.searchMentorsWithApprovedCV(any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(dummyMentors));
        when(mentorService.getAllMentorExpertiseFromApprovedCVs()).thenReturn(List.of());

        String view = mentorController.viewMentorList(null, new String[]{}, null, null, null, null, 1, size, null, model);
        assertEquals("mentee/mentor-list", view);
    }

    private Mentor createMentor(String fullName, List<String> skills) {
        Mentor mentor = new Mentor();
        mentor.setFullName(fullName);
        mentor.setExpertise(String.join(",", skills));
        mentor.setAvailable(true);
        mentor.setTotalSessions(10);
        mentor.setRating(4.8);
        return mentor;
    }
}
