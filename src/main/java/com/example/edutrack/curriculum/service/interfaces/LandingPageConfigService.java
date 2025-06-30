package com.example.edutrack.curriculum.service.interfaces;

import com.example.edutrack.curriculum.model.LandingPageConfig;
import com.example.edutrack.curriculum.model.MenteeLandingRole;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface LandingPageConfigService {

    LandingPageConfig getConfigByRole(MenteeLandingRole role);

    LandingPageConfig resetToCurrent(MenteeLandingRole role);

    void save(LandingPageConfig config ,MenteeLandingRole role);

    String handleImageUpload(MultipartFile file, String existingUrl) throws IOException;

    void syncGuestConfigToAllRoles(boolean usePersonalization);

    boolean isPersonalizationEnabled();

    void setPersonalizationMode(boolean isEnabled);
}
