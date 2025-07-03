package com.example.edutrack.curriculum.service.implementation;

import com.example.edutrack.curriculum.model.LandingPageConfig;
import com.example.edutrack.curriculum.model.MenteeLandingRole;
import com.example.edutrack.curriculum.repository.LandingPageConfigRepository;
import com.example.edutrack.curriculum.service.interfaces.LandingPageConfigService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class LandingPageConfigServiceImpl implements LandingPageConfigService {

    private final LandingPageConfigRepository configRepository;

    public LandingPageConfigServiceImpl(LandingPageConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public LandingPageConfig getConfigByRole(MenteeLandingRole role) {
        return configRepository.findByRole(role)
                .orElseThrow(() -> new IllegalArgumentException("No landing page config found for role: " + role));
    }

    @Override
    public LandingPageConfig resetToCurrent(MenteeLandingRole role) {
        return getConfigByRole(role);
    }

    private void syncFooterAcrossRoles(String description, String copyright, MenteeLandingRole exclude) {
        for (MenteeLandingRole r : MenteeLandingRole.values()) {
            if (r == exclude) continue;
            configRepository.findByRole(r).ifPresent(other -> {
                other.setFooterDescription(description);
                other.setCopyrightText(copyright);
                configRepository.save(other);
            });
        }
    }

    // Hàm F3
    @Override
    @Transactional
    public void save(LandingPageConfig updatedConfig, MenteeLandingRole role) {
        LandingPageConfig existing = configRepository.findByRole(role)
                .orElseThrow(() -> new IllegalArgumentException("No landing page config found for role: " + role));

        existing.setHeroHeadline(updatedConfig.getHeroHeadline());
        existing.setHeroSubHeadline(updatedConfig.getHeroSubHeadline());
        existing.setHeroCTA(updatedConfig.getHeroCTA());
        existing.setHeroCTALink(updatedConfig.getHeroCTALink());

        existing.setCategoryTitle(updatedConfig.getCategoryTitle());
        existing.setCategorySubtitle(updatedConfig.getCategorySubtitle());
        existing.setCategoryButtonText(updatedConfig.getCategoryButtonText());

        existing.setAboutTitle(updatedConfig.getAboutTitle());
        existing.setAboutSubtitle(updatedConfig.getAboutSubtitle());
        existing.setAboutDescription(updatedConfig.getAboutDescription());

        existing.setSectionOneTitle(updatedConfig.getSectionOneTitle());
        existing.setSectionOneSubtitle(updatedConfig.getSectionOneSubtitle());
        existing.setSectionTwoTitle(updatedConfig.getSectionTwoTitle());
        existing.setSectionTwoSubtitle(updatedConfig.getSectionTwoSubtitle());

        existing.setCourseSectionOneSuggestion(updatedConfig.getCourseSectionOneSuggestion());
        existing.setCourseSectionTwoSuggestion(updatedConfig.getCourseSectionTwoSuggestion());
        existing.setTagSuggestion(updatedConfig.getTagSuggestion());
        existing.setMentorSuggestion(updatedConfig.getMentorSuggestion());

        existing.setMentorSectionTitle(updatedConfig.getMentorSectionTitle());
        existing.setMentorSectionSubtitle(updatedConfig.getMentorSectionSubtitle());

        existing.setFooterDescription(updatedConfig.getFooterDescription());
        existing.setCopyrightText(updatedConfig.getCopyrightText());

        if (role == MenteeLandingRole.MENTEE_EXPERIENCED) {
            existing.setUseScheduleReminder(updatedConfig.isUseScheduleReminder());
        } else {
            existing.setUseScheduleReminder(false);
        }
        existing.setHeroImageUrl(updatedConfig.getHeroImageUrl());
        existing.setCategorySectionBgUrl(updatedConfig.getCategorySectionBgUrl());
        existing.setAboutSectionImageUrl(updatedConfig.getAboutSectionImageUrl());
        existing.setCourseSectionBgUrl(updatedConfig.getCourseSectionBgUrl());
        existing.setMentorSectionBgUrl(updatedConfig.getMentorSectionBgUrl());
        System.out.println(">> Role = " + role);
        System.out.println(">> isScheduleReminder (from form) = " + updatedConfig.isUseScheduleReminder());

        configRepository.save(existing);

        syncFooterAcrossRoles(
                updatedConfig.getFooterDescription(),
                updatedConfig.getCopyrightText(),
                role
        );
    }


    @Override
    public String handleImageUpload(MultipartFile file, String existingUrl) throws IOException {
        if (file == null || file.isEmpty()) return existingUrl;

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String uploadDirPath = new File("uploads").getAbsolutePath(); // = /project-root/uploads
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File dest = new File(uploadDir, filename);
        file.transferTo(dest);
        return "/uploads/" + filename;
    }



    @Override
    public void syncGuestConfigToAllRoles(boolean usePersonalization) {
        LandingPageConfig guest = configRepository.findByRole(MenteeLandingRole.GUEST)
                .orElseThrow(() -> new RuntimeException("Guest config not found"));

        for (MenteeLandingRole role : MenteeLandingRole.values()) {
            if (role != MenteeLandingRole.GUEST) {
                LandingPageConfig copied = new LandingPageConfig(guest);
                copied.setRole(role);
                copied.setUsePersonalization(usePersonalization);

                configRepository.findByRole(role).ifPresentOrElse(
                        existing -> {
                            copied.setId(existing.getId());
                            configRepository.save(copied);
                        },
                        () -> configRepository.save(copied)
                );
            }
        }
    }

    @Override
    public boolean isPersonalizationEnabled() {
        return getConfigByRole(MenteeLandingRole.GUEST).isUsePersonalization();
    }

    @Override
    public void setPersonalizationMode(boolean value) {
        LandingPageConfig guestConfig = getConfigByRole(MenteeLandingRole.GUEST);
        guestConfig.setUsePersonalization(value);
        configRepository.save(guestConfig);
        if (!value) {
            syncGuestConfigToAllRoles(false);
        } else {
            for (MenteeLandingRole role : MenteeLandingRole.values()) {
                configRepository.findByRole(role).ifPresent(config -> {
                    config.setUsePersonalization(true);
                    configRepository.save(config);
                });
            }
        }
    }





}


