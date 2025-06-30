package com.example.edutrack;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.model.User;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.profiles.dto.CVForm;
import com.example.edutrack.profiles.model.CV;
import com.example.edutrack.profiles.service.CvServiceImpl;
import com.example.edutrack.profiles.repository.CvRepository;
import com.example.edutrack.profiles.service.interfaces.CvService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class CVServiceTest_ValidateEntitiesAndBuildCV {
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorRepository mentorRepository;
    @Mock
    private CvRepository cvRepository;

    @InjectMocks
    private CvServiceImpl cvService;

    @Test
    void userNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        CVForm form = new CVForm();
        form.setUserId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cvService.validateEntitiesAndBuildCV(form, UUID.randomUUID())
        );
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void mentorNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        UUID mentorId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        CVForm form = new CVForm();
        form.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mentorRepository.findById(mentorId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> cvService.validateEntitiesAndBuildCV(form, mentorId)
        );
        assertEquals("Mentor not found", ex.getMessage());
    }

    @Test
    void allFieldsPresent_cvBuiltAndSaved() {
        UUID userId = UUID.randomUUID();
        UUID mentorId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        Mentor mentor = new Mentor(); mentor.setId(mentorId);

        CVForm form = new CVForm();
        form.setUserId(userId);
        form.setSummary("summaryValue");
        form.setExperienceYears(7);
        form.setSkills("Java, Spring");
        form.setEducation("Bachelor Degree");
        form.setExperience("Worked at Company X");
        form.setCertifications("CertA, CertB");
        form.setLanguages("English, Vietnamese");
        form.setPortfolioUrl("http://portfolio.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(mentor));

        doAnswer(invocation -> invocation.getArgument(0)).when(cvRepository).save(any(CV.class));

        CV cv = cvService.validateEntitiesAndBuildCV(form, mentorId);

        verify(cvRepository).save(any(CV.class));
        assertEquals("summaryValue", cv.getSummary());
        assertEquals(7, cv.getExperienceYears());
        assertEquals("Java, Spring", cv.getSkills());
        assertEquals("Bachelor Degree", cv.getEducation());
        assertEquals("Worked at Company X", cv.getExperience());
        assertEquals("CertA, CertB", cv.getCertifications());
        assertEquals("English, Vietnamese", cv.getLanguages());
        assertEquals("http://portfolio.com", cv.getPortfolioUrl());
        assertEquals(user, cv.getUser());
        assertEquals(userId, cv.getId());
    }

    @Test
    void someFieldsNull_cvBuiltWithNulls() {
        UUID userId = UUID.randomUUID();
        UUID mentorId = UUID.randomUUID();
        User user = new User(); user.setId(userId);
        Mentor mentor = new Mentor(); mentor.setId(mentorId);

        CVForm form = new CVForm();
        form.setUserId(userId);
        form.setSummary(null);
        form.setExperienceYears(2);
        form.setSkills(null);
        form.setEducation(null);
        form.setExperience(null);
        form.setCertifications(null);
        form.setLanguages(null);
        form.setPortfolioUrl(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mentorRepository.findById(mentorId)).thenReturn(Optional.of(mentor));

        doAnswer(invocation -> invocation.getArgument(0)).when(cvRepository).save(any(CV.class));

        CV cv = cvService.validateEntitiesAndBuildCV(form, mentorId);

        verify(cvRepository).save(any(CV.class));
        assertNull(cv.getSummary());
        assertEquals(2, cv.getExperienceYears());
        assertNull(cv.getSkills());
        assertNull(cv.getEducation());
        assertNull(cv.getExperience());
        assertNull(cv.getCertifications());
        assertNull(cv.getLanguages());
        assertNull(cv.getPortfolioUrl());
        assertEquals(user, cv.getUser());
        assertEquals(userId, cv.getId());
    }
}
