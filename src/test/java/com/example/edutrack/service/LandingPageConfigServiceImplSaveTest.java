package com.example.edutrack.service;

import com.example.edutrack.curriculum.model.LandingPageConfig;
import com.example.edutrack.curriculum.model.MenteeLandingRole;
import com.example.edutrack.curriculum.model.SuggestionType;
import com.example.edutrack.curriculum.repository.LandingPageConfigRepository;
import com.example.edutrack.curriculum.service.implementation.LandingPageConfigServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Hàm F3: public void save(LandingPageConfig config, MenteeLandingRole role)
// trong LandingPageConfigServiceImpl
@ExtendWith(MockitoExtension.class)
@DisplayName("LandingPageConfigServiceImpl - save() Method Tests")
class LandingPageConfigServiceImplSaveTest {

    @Mock
    private LandingPageConfigRepository configRepository;

    @InjectMocks
    private LandingPageConfigServiceImpl landingPageConfigService;

    private LandingPageConfig existingConfig;
    private LandingPageConfig updatedConfig;
    private LandingPageConfig otherRoleConfig1;
    private LandingPageConfig otherRoleConfig2;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        existingConfig = new LandingPageConfig();
        existingConfig.setId(1L);
        existingConfig.setRole(MenteeLandingRole.MENTEE_EXPERIENCED);
        existingConfig.setHeroHeadline("Old Headline");
        existingConfig.setHeroSubHeadline("Old Sub Headline");
        existingConfig.setUseScheduleReminder(false);
        existingConfig.setFooterDescription("Old Footer");
        existingConfig.setCopyrightText("Old Copyright");

        updatedConfig = new LandingPageConfig();
        updatedConfig.setHeroHeadline("New Headline");
        updatedConfig.setHeroSubHeadline("New Sub Headline");
        updatedConfig.setHeroCTA("New CTA");
        updatedConfig.setHeroCTALink("https://new-cta.com");

        updatedConfig.setCategoryTitle("New Category Title");
        updatedConfig.setCategorySubtitle("New Category Subtitle");
        updatedConfig.setCategoryButtonText("New Button Text");

        updatedConfig.setAboutTitle("New About Title");
        updatedConfig.setAboutSubtitle("New About Subtitle");
        updatedConfig.setAboutDescription("New About Description");

        updatedConfig.setSectionOneTitle("New Section One Title");
        updatedConfig.setSectionOneSubtitle("New Section One Subtitle");
        updatedConfig.setSectionTwoTitle("New Section Two Title");
        updatedConfig.setSectionTwoSubtitle("New Section Two Subtitle");

        updatedConfig.setCourseSectionOneSuggestion(SuggestionType.POPULAR);
        updatedConfig.setCourseSectionTwoSuggestion(SuggestionType.TRENDING);
        updatedConfig.setTagSuggestion(SuggestionType.INTEREST_BASED);
        updatedConfig.setMentorSuggestion(SuggestionType.TOP_RATED);

        updatedConfig.setMentorSectionTitle("New Mentor Section Title");
        updatedConfig.setMentorSectionSubtitle("New Mentor Section Subtitle");

        updatedConfig.setFooterDescription("New Footer Description");
        updatedConfig.setCopyrightText("New Copyright Text");

        updatedConfig.setUseScheduleReminder(true);

        updatedConfig.setHeroImageUrl("/images/new-hero.jpg");
        updatedConfig.setCategorySectionBgUrl("/images/new-category-bg.jpg");
        updatedConfig.setAboutSectionImageUrl("/images/new-about.jpg");
        updatedConfig.setCourseSectionBgUrl("/images/new-course-bg.jpg");
        updatedConfig.setMentorSectionBgUrl("/images/new-mentor-bg.jpg");

        otherRoleConfig1 = new LandingPageConfig();
        otherRoleConfig1.setId(2L);
        otherRoleConfig1.setRole(MenteeLandingRole.MENTEE_NEW);
        otherRoleConfig1.setFooterDescription("Other Footer 1");
        otherRoleConfig1.setCopyrightText("Other Copyright 1");

        otherRoleConfig2 = new LandingPageConfig();
        otherRoleConfig2.setId(3L);
        otherRoleConfig2.setRole(MenteeLandingRole.GUEST);
        otherRoleConfig2.setFooterDescription("Other Footer 2");
        otherRoleConfig2.setCopyrightText("Other Copyright 2");
    }

    /**
     * TC 3.1: Kiểm tra lưu cấu hình MENTEE_EXPERIENCED với useScheduleReminder=true
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Role là MENTEE_EXPERIENCED
     *   - VP2: Cấu hình hiện có tồn tại (findByRole trả về Optional có giá trị)
     *   - VP3: useScheduleReminder = true
     *   - VP4: Các trường cấu hình hợp lệ (hero, category, about, course, mentor, footer)
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh role == MENTEE_EXPERIENCED
     *   - Nhánh giữ nguyên useScheduleReminder từ config đầu vào
     *   - Nhánh đồng bộ footer cho các role khác
     * Mục đích: Đảm bảo hàm lưu đúng cấu hình trang đích cho vai trò MENTEE_EXPERIENCED trong Youdemi, giữ useScheduleReminder=true và đồng bộ footer.
     */
    @Test
    @DisplayName("Should save MENTEE_EXPERIENCED config with useScheduleReminder=true")
    void testSave_MenteeExperienced_WithScheduleReminderTrue() {
        // Given
        when(configRepository.findByRole(MenteeLandingRole.MENTEE_EXPERIENCED))
                .thenReturn(Optional.of(existingConfig));

        // Setup footer sync mocks
        when(configRepository.findByRole(MenteeLandingRole.MENTEE_NEW))
                .thenReturn(Optional.of(otherRoleConfig1));
        when(configRepository.findByRole(MenteeLandingRole.GUEST))
                .thenReturn(Optional.of(otherRoleConfig2));

        // When
        landingPageConfigService.save(updatedConfig, MenteeLandingRole.MENTEE_EXPERIENCED);

        // Then
        ArgumentCaptor<LandingPageConfig> configCaptor = ArgumentCaptor.forClass(LandingPageConfig.class);
        verify(configRepository, times(3)).save(configCaptor.capture()); // 1 main + 2 footer sync

        // Verify main config save
        LandingPageConfig savedMainConfig = configCaptor.getAllValues().get(0);
        assertThat(savedMainConfig.getHeroHeadline()).isEqualTo("New Headline");
        assertThat(savedMainConfig.getHeroSubHeadline()).isEqualTo("New Sub Headline");
        assertThat(savedMainConfig.getHeroCTA()).isEqualTo("New CTA");
        assertThat(savedMainConfig.getHeroCTALink()).isEqualTo("https://new-cta.com");

        // Category section
        assertThat(savedMainConfig.getCategoryTitle()).isEqualTo("New Category Title");
        assertThat(savedMainConfig.getCategorySubtitle()).isEqualTo("New Category Subtitle");
        assertThat(savedMainConfig.getCategoryButtonText()).isEqualTo("New Button Text");

        // About section
        assertThat(savedMainConfig.getAboutTitle()).isEqualTo("New About Title");
        assertThat(savedMainConfig.getAboutSubtitle()).isEqualTo("New About Subtitle");
        assertThat(savedMainConfig.getAboutDescription()).isEqualTo("New About Description");

        // Course sections
        assertThat(savedMainConfig.getSectionOneTitle()).isEqualTo("New Section One Title");
        assertThat(savedMainConfig.getSectionTwoTitle()).isEqualTo("New Section Two Title");
        assertThat(savedMainConfig.getCourseSectionOneSuggestion()).isEqualTo(SuggestionType.POPULAR);
        assertThat(savedMainConfig.getCourseSectionTwoSuggestion()).isEqualTo(SuggestionType.TRENDING);

        // Mentor section
        assertThat(savedMainConfig.getMentorSectionTitle()).isEqualTo("New Mentor Section Title");
        assertThat(savedMainConfig.getMentorSectionSubtitle()).isEqualTo("New Mentor Section Subtitle");

        // Footer
        assertThat(savedMainConfig.getFooterDescription()).isEqualTo("New Footer Description");
        assertThat(savedMainConfig.getCopyrightText()).isEqualTo("New Copyright Text");

        // Images
        assertThat(savedMainConfig.getHeroImageUrl()).isEqualTo("/images/new-hero.jpg");
        assertThat(savedMainConfig.getCategorySectionBgUrl()).isEqualTo("/images/new-category-bg.jpg");

        // CRITICAL: MENTEE_EXPERIENCED should keep useScheduleReminder=true
        assertThat(savedMainConfig.isUseScheduleReminder()).isTrue();

        // Verify footer sync was called for other roles
        verify(configRepository).findByRole(MenteeLandingRole.MENTEE_NEW);
        verify(configRepository).findByRole(MenteeLandingRole.GUEST);
    }

    /**
     * TC 3.2: Kiểm tra lưu cấu hình MENTEE_EXPERIENCED với useScheduleReminder=false
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Role là MENTEE_EXPERIENCED
     *   - VP2: Cấu hình hiện có tồn tại
     *   - VP5: useScheduleReminder = false
     *   - VP4: Các trường cấu hình hợp lệ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh role == MENTEE_EXPERIENCED
     *   - Nhánh giữ nguyên useScheduleReminder từ config đầu vào
     * Mục đích: Đảm bảo hàm lưu đúng cấu hình trang đích cho vai trò MENTEE_EXPERIENCED trong Youdemi với useScheduleReminder=false.
     */
    @Test
    @DisplayName("Should save MENTEE_EXPERIENCED config with useScheduleReminder=false when form value is false")
    void testSave_MenteeExperienced_WithScheduleReminderFalse() {
        updatedConfig.setUseScheduleReminder(false);

        mockEmptyFooterSync();

        when(configRepository.findByRole(MenteeLandingRole.MENTEE_EXPERIENCED))
                .thenReturn(Optional.of(existingConfig)); // Then override it

        landingPageConfigService.save(updatedConfig, MenteeLandingRole.MENTEE_EXPERIENCED);

        ArgumentCaptor<LandingPageConfig> configCaptor = ArgumentCaptor.forClass(LandingPageConfig.class);
        verify(configRepository, atLeastOnce()).save(configCaptor.capture());

        LandingPageConfig savedConfig = configCaptor.getAllValues().get(0);
        assertThat(savedConfig.isUseScheduleReminder()).isFalse();
    }

    /**
     * TC 3.3: Kiểm tra lưu cấu hình cho role không phải MENTEE_EXPERIENCED, ép useScheduleReminder=false
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP6: Role là MENTEE_NEW
     *   - VP2: Cấu hình hiện có tồn tại
     *   - VP3: useScheduleReminder = true (nhưng sẽ bị ép thành false)
     *   - VP4: Các trường cấu hình hợp lệ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh role != MENTEE_EXPERIENCED
     *   - Nhánh ép useScheduleReminder = false
     * Mục đích: Đảm bảo hàm ép useScheduleReminder=false cho các vai trò không phải MENTEE_EXPERIENCED trong Youdemi.
     */
    @Test
    @DisplayName("Should save non-MENTEE_EXPERIENCED role with useScheduleReminder=false regardless of input")
    void testSave_NonMenteeExperienced_ShouldForceScheduleReminderFalse() {
        mockEmptyFooterSync();
        updatedConfig.setUseScheduleReminder(true); // Form sends true but should be ignored

        LandingPageConfig newConfig = new LandingPageConfig();
        newConfig.setId(2L);
        newConfig.setRole(MenteeLandingRole.MENTEE_NEW);

        when(configRepository.findByRole(MenteeLandingRole.MENTEE_NEW))
                .thenReturn(Optional.of(newConfig));

        // When
        landingPageConfigService.save(updatedConfig, MenteeLandingRole.MENTEE_NEW);

        // Then
        ArgumentCaptor<LandingPageConfig> configCaptor = ArgumentCaptor.forClass(LandingPageConfig.class);
        verify(configRepository, atLeastOnce()).save(configCaptor.capture());

        LandingPageConfig savedConfig = configCaptor.getAllValues().get(0);
        // CRITICAL: Non-MENTEE_EXPERIENCED roles should always have useScheduleReminder=false
        assertThat(savedConfig.isUseScheduleReminder()).isFalse();
    }

    /**
     * TC 3.4: Kiểm tra lưu cấu hình cho role GUEST, ép useScheduleReminder=false
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP7: Role là GUEST
     *   - VP2: Cấu hình hiện có tồn tại
     *   - VP3: useScheduleReminder = true (nhưng sẽ bị ép thành false)
     *   - VP4: Các trường cấu hình hợp lệ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh role != MENTEE_EXPERIENCED
     *   - Nhánh ép useScheduleReminder = false
     * Mục đích: Đảm bảo hàm ép useScheduleReminder=false cho vai trò GUEST trong Youdemi.
     */
    @Test
    @DisplayName("Should save GUEST role with useScheduleReminder=false")
    void testSave_GuestRole_ShouldForceScheduleReminderFalse() {
        mockEmptyFooterSync();
        updatedConfig.setUseScheduleReminder(true);

        LandingPageConfig guestConfig = new LandingPageConfig();
        guestConfig.setId(3L);
        guestConfig.setRole(MenteeLandingRole.GUEST);

        when(configRepository.findByRole(MenteeLandingRole.GUEST))
                .thenReturn(Optional.of(guestConfig));

        // When
        landingPageConfigService.save(updatedConfig, MenteeLandingRole.GUEST);

        // Then
        ArgumentCaptor<LandingPageConfig> configCaptor = ArgumentCaptor.forClass(LandingPageConfig.class);
        verify(configRepository, atLeastOnce()).save(configCaptor.capture());

        LandingPageConfig savedConfig = configCaptor.getAllValues().get(0);
        assertThat(savedConfig.isUseScheduleReminder()).isFalse();
    }

    /**
     * TC 3.5: Kiểm tra đồng bộ footer cho tất cả các role khác trừ role hiện tại
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Role là MENTEE_EXPERIENCED
     *   - VP2: Cấu hình hiện có tồn tại
     *   - VP8: Các role khác (MENTEE_NEW, GUEST) tồn tại
     *   - VP4: Các trường cấu hình hợp lệ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh đồng bộ footer cho các role khác
     *   - Nhánh kiểm tra tất cả giá trị enum MenteeLandingRole
     * Mục đích: Đảm bảo hàm đồng bộ footerDescription và copyrightText cho các vai trò khác trong Youdemi.
     */
    @Test
    @DisplayName("Should sync footer to all other roles except the current role")
    void testSave_ShouldSyncFooterAcrossRoles() {
        // Given - mock the main config being updated (MENTEE_EXPERIENCED)
        when(configRepository.findByRole(MenteeLandingRole.MENTEE_EXPERIENCED))
                .thenReturn(Optional.of(existingConfig));

        // Mock other role configs to receive the footer sync
        when(configRepository.findByRole(MenteeLandingRole.MENTEE_NEW))
                .thenReturn(Optional.of(otherRoleConfig1));
        when(configRepository.findByRole(MenteeLandingRole.GUEST))
                .thenReturn(Optional.of(otherRoleConfig2));

        // When - saving for MENTEE_EXPERIENCED role
        landingPageConfigService.save(updatedConfig, MenteeLandingRole.MENTEE_EXPERIENCED);

        // Then
        // Capture all saved configs (1 main save + 2 synced roles)
        ArgumentCaptor<LandingPageConfig> configCaptor = ArgumentCaptor.forClass(LandingPageConfig.class);
        verify(configRepository, times(3)).save(configCaptor.capture());

        // Extract saved configs
        List<LandingPageConfig> savedConfigs = configCaptor.getAllValues();

        // Assert the main config was saved (at index 0)
        LandingPageConfig mainSaved = savedConfigs.get(0);
        assertThat(mainSaved.getRole()).isEqualTo(MenteeLandingRole.MENTEE_EXPERIENCED);
        assertThat(mainSaved.getFooterDescription()).isEqualTo("New Footer Description");

        // Assert footer was synced to MENTEE_NEW and GUEST (index 1 and 2)
        for (int i = 1; i < savedConfigs.size(); i++) {
            LandingPageConfig synced = savedConfigs.get(i);
            assertThat(synced.getFooterDescription()).isEqualTo("New Footer Description");
            assertThat(synced.getCopyrightText()).isEqualTo("New Copyright Text");
        }

        // Verify each expected role was queried
        verify(configRepository, times(1)).findByRole(MenteeLandingRole.MENTEE_EXPERIENCED); // For main update
        verify(configRepository, times(1)).findByRole(MenteeLandingRole.MENTEE_NEW);         // For sync
        verify(configRepository, times(1)).findByRole(MenteeLandingRole.GUEST);              // For sync

        verifyNoMoreInteractions(configRepository);
    }

    /**
     * TC 3.6: Kiểm tra không đồng bộ footer cho role đang được cập nhật
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP7: Role là GUEST
     *   - VP2: Cấu hình hiện có tồn tại
     *   - VP8: Các role khác (MENTEE_NEW, MENTEE_EXPERIENCED) tồn tại
     *   - VP4: Các trường cấu hình hợp lệ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh loại trừ role hiện tại khỏi danh sách đồng bộ footer
     * Mục đích: Đảm bảo hàm không đồng bộ footer cho vai trò GUEST đang được cập nhật trong Youdemi.
     */
    @Test
    @DisplayName("Should not sync footer to the same role being updated")
    void testSave_ShouldExcludeCurrentRoleFromFooterSync() {
        // Given
        when(configRepository.findByRole(MenteeLandingRole.GUEST))
                .thenReturn(Optional.of(otherRoleConfig2));

        // Setup other roles
        when(configRepository.findByRole(MenteeLandingRole.MENTEE_NEW))
                .thenReturn(Optional.of(otherRoleConfig1));
        when(configRepository.findByRole(MenteeLandingRole.MENTEE_EXPERIENCED))
                .thenReturn(Optional.of(existingConfig));

        // When - Save GUEST role
        landingPageConfigService.save(updatedConfig, MenteeLandingRole.GUEST);

        // Then - Verify that GUEST role was not queried for footer sync
        verify(configRepository, times(1)).findByRole(MenteeLandingRole.GUEST); // Only for main save

        // But other roles should be queried for sync
        verify(configRepository).findByRole(MenteeLandingRole.MENTEE_NEW);
        verify(configRepository).findByRole(MenteeLandingRole.MENTEE_EXPERIENCED);
    }

    /**
     * TC 3.7: Kiểm tra trường hợp không tìm thấy cấu hình cho role
     * Phân vùng tương đương (Equivalence Partition):
     *   - IP1: Cấu hình không tồn tại (findByRole trả về Optional rỗng)
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh ném IllegalArgumentException khi findByRole trả về Optional rỗng
     * Mục đích: Đảm bảo hàm ném ngoại lệ khi không tìm thấy cấu hình cho vai trò MENTEE_EXPERIENCED trong Youdemi.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException when config not found for role")
    void testSave_ConfigNotFound_ShouldThrowException() {
        // Given
        when(configRepository.findByRole(MenteeLandingRole.MENTEE_EXPERIENCED))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() ->
                landingPageConfigService.save(updatedConfig, MenteeLandingRole.MENTEE_EXPERIENCED)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No landing page config found for role: MENTEE_EXPERIENCED");

        // Verify no save operations were attempted
        verify(configRepository, never()).save(any());
    }

    /**
     * TC 3.8: Kiểm tra xử lý tất cả giá trị enum MenteeLandingRole trong đồng bộ footer
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Role là MENTEE_EXPERIENCED
     *   - VP2: Cấu hình hiện có tồn tại
     *   - IP2: Các role khác không có cấu hình (Optional rỗng)
     *   - VP4: Các trường cấu hình hợp lệ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh lặp qua tất cả giá trị enum MenteeLandingRole
     *   - Nhánh bỏ qua role hiện tại trong đồng bộ footer
     * Mục đích: Đảm bảo hàm kiểm tra tất cả giá trị enum MenteeLandingRole khi đồng bộ footer trong Youdemi.
     */
    @Test
    @DisplayName("Should handle all MenteeLandingRole enum values in footer sync")
    void testSave_ShouldHandleAllEnumValues() {
        // Given
        when(configRepository.findByRole(MenteeLandingRole.MENTEE_EXPERIENCED))
                .thenReturn(Optional.of(existingConfig));

        // Mock all possible enum values
        for (MenteeLandingRole role : MenteeLandingRole.values()) {
            if (role != MenteeLandingRole.MENTEE_EXPERIENCED) {
                when(configRepository.findByRole(role))
                        .thenReturn(Optional.empty());
            }
        }

        // When
        landingPageConfigService.save(updatedConfig, MenteeLandingRole.MENTEE_EXPERIENCED);

        // Then - Verify all enum values were checked except the current one
        for (MenteeLandingRole role : MenteeLandingRole.values()) {
            if (role != MenteeLandingRole.MENTEE_EXPERIENCED) {
                verify(configRepository).findByRole(role);
            }
        }
    }

    /**
     * TC 3.9: Kiểm tra giữ nguyên ID hiện có khi cập nhật cấu hình
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Role là MENTEE_EXPERIENCED
     *   - VP2: Cấu hình hiện có tồn tại
     *   - VP4: Các trường cấu hình hợp lệ
     *   - VP9: ID hiện có hợp lệ
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh giữ nguyên ID của cấu hình hiện có
     * Mục đích: Đảm bảo hàm giữ nguyên ID của cấu hình hiện có khi cập nhật trong Youdemi.
     */
    @Test
    @DisplayName("Should preserve existing ID when updating")
    void testSave_ShouldPreserveExistingId() {
        mockEmptyFooterSync();
        existingConfig.setId(999L); // Specific ID to verify preservation

        when(configRepository.findByRole(MenteeLandingRole.MENTEE_EXPERIENCED))
                .thenReturn(Optional.of(existingConfig));
        // When
        landingPageConfigService.save(updatedConfig, MenteeLandingRole.MENTEE_EXPERIENCED);

        // Then
        ArgumentCaptor<LandingPageConfig> configCaptor = ArgumentCaptor.forClass(LandingPageConfig.class);
        verify(configRepository, atLeastOnce()).save(configCaptor.capture());

        LandingPageConfig savedConfig = configCaptor.getAllValues().get(0);
        assertThat(savedConfig.getId()).isEqualTo(999L); // Should preserve existing ID
    }

    /**
     * TC 3.10: Kiểm tra xử lý an toàn với các giá trị null trong config đầu vào
     * Phân vùng tương đương (Equivalence Partition):
     *   - VP1: Role là MENTEE_EXPERIENCED
     *   - VP2: Cấu hình hiện có tồn tại
     *   - IP3: Một số trường trong config đầu vào là null
     *   - VP5: useScheduleReminder = false
     * Độ phủ nhánh (Branch Coverage):
     *   - Nhánh xử lý các trường null trong config đầu vào
     * Mục đích: Đảm bảo hàm xử lý an toàn các giá trị null trong cấu hình đầu vào khi lưu trong Youdemi.
     */
    @Test
    @DisplayName("Should handle null values in updatedConfig gracefully")
    void testSave_WithNullValues_ShouldHandleGracefully() {
        mockEmptyFooterSync();
        LandingPageConfig configWithNulls = new LandingPageConfig();
        configWithNulls.setHeroHeadline(null);
        configWithNulls.setUseScheduleReminder(false);
        configWithNulls.setFooterDescription(null);
        configWithNulls.setCopyrightText(null);

        when(configRepository.findByRole(MenteeLandingRole.MENTEE_EXPERIENCED))
                .thenReturn(Optional.of(existingConfig));

        // When
        landingPageConfigService.save(configWithNulls, MenteeLandingRole.MENTEE_EXPERIENCED);

        // Then
        ArgumentCaptor<LandingPageConfig> configCaptor = ArgumentCaptor.forClass(LandingPageConfig.class);
        verify(configRepository, atLeastOnce()).save(configCaptor.capture());

        LandingPageConfig savedConfig = configCaptor.getAllValues().get(0);
        assertThat(savedConfig.getHeroHeadline()).isNull();
        assertThat(savedConfig.getFooterDescription()).isNull();
        assertThat(savedConfig.getCopyrightText()).isNull();
    }

    private void mockEmptyFooterSync() {
        for (MenteeLandingRole role : MenteeLandingRole.values()) {
            when(configRepository.findByRole(role)).thenReturn(Optional.empty());
        }
    }
}