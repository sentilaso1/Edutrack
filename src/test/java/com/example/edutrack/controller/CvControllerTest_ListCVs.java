package com.example.edutrack.controller;

import com.example.edutrack.profiles.controller.CvController;
import com.example.edutrack.profiles.dto.CVFilterForm;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.service.interfaces.CvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CvControllerTest_ListCVs {

    @Mock
    private CvService cvService;

    @Mock
    private Model model;

    @InjectMocks
    private CvController controller;

    private CVFilterForm form;
    private List<CV> dummyCVs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        form = new CVFilterForm();
        dummyCVs = List.of(new CV(), new CV());
    }

    @Nested
    class ListCVsValidCases {

        @Test
        void shouldReturnListCVPage_forValidInput() {
            form.setFilter(CVFilterForm.FILTER_APPROVED);
            form.setSort(CVFilterForm.SORT_DATE_ASC);
            form.setTags(List.of("java"));

            when(cvService.getAllUniqueSkills()).thenReturn(List.of("java"));
            when(cvService.isBatchRunning()).thenReturn(false);
            when(cvService.getLastBatchEnd()).thenReturn(LocalDateTime.now().minusMinutes(10));
            when(cvService.queryCVs(any(), any(), any(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(dummyCVs));

            String viewName = controller.listCVs(form, model, 1);

            verify(model).addAttribute(eq("pageNumber"), eq(1));
            verify(model).addAttribute(eq("page"), any(Page.class));
            verify(model).addAttribute(eq("skills"), anyList());
            verify(model).addAttribute(eq("nextBatchMillis"), anyLong());
            verify(model).addAttribute(eq("batchRunning"), anyBoolean());
            verify(model).addAttribute(eq("filter"), eq(CVFilterForm.FILTER_APPROVED));
            verify(model).addAttribute(eq("sort"), eq(CVFilterForm.SORT_DATE_ASC));
            verify(model).addAttribute(eq("tags"), eq(List.of("java")));

            assertEquals("/cv/list-cv", viewName);
        }

        @Test
        void shouldReturnListCVPage_whenResultIsEmpty() {
            form.setFilter(CVFilterForm.FILTER_APPROVED);
            form.setSort(CVFilterForm.SORT_DATE_ASC);
            form.setTags(List.of("java"));

            when(cvService.getAllUniqueSkills()).thenReturn(List.of("java"));
            when(cvService.isBatchRunning()).thenReturn(true);
            when(cvService.getLastBatchEnd()).thenReturn(LocalDateTime.now());
            when(cvService.queryCVs(any(), any(), any(), any(Pageable.class)))
                    .thenReturn(Page.empty());

            String view = controller.listCVs(form, model, 1);
            assertEquals("/cv/list-cv", view);
        }
    }

    @Nested
    class ListCVsInvalidCases {

        @Test
        void shouldRedirectToError_whenInvalidFilter() {
            assertThrows(IllegalArgumentException.class, () -> form.setFilter("invalid"));
        }

        @Test
        void shouldRedirectToError_whenInvalidSort() {
            assertThrows(IllegalArgumentException.class, () -> form.setSort("invalid"));
        }

        @Test
        void shouldRedirectToError_whenInvalidTag() {
            form.setTags(List.of("java", "??"));
            form.setFilter(CVFilterForm.FILTER_APPROVED);
            form.setSort(CVFilterForm.SORT_DATE_ASC);

            when(cvService.getAllUniqueSkills()).thenReturn(List.of("java"));
            when(cvService.queryCVs(any(), any(), any(), any(Pageable.class)))
                    .thenThrow(IllegalArgumentException.class);

            String view = controller.listCVs(form, model, 1);
            assertEquals("redirect:/error", view);
        }

        @ParameterizedTest
        @NullAndEmptySource
        void shouldRedirectToError_whenFilterIsNullOrEmpty(String invalidFilter) {
            assertDoesNotThrow(() -> {
                form.setFilter(CVFilterForm.FILTER_APPROVED);
                form.setSort(CVFilterForm.SORT_DATE_ASC);
                form.setTags(List.of("java"));
                controller.listCVs(form, model, 0);
            });
        }
    }
}