package com.example.edutrack.auth.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.repository.MenteeRepository;
import com.example.edutrack.accounts.repository.MentorRepository;
import com.example.edutrack.accounts.repository.UserRepository;
import com.example.edutrack.auth.service.interfaces.GoogleOAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockHttpSession;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleAuthControllerTest {

    @Mock
    private GoogleOAuthService googleOAuthService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MenteeRepository menteeRepository;
    @Mock
    private MentorRepository mentorRepository;

    @InjectMocks
    private GoogleAuthController googleAuthController;

    private MockHttpSession session;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        session = new MockHttpSession();
    }

    @Test
    void googleLogin_shouldRedirectToGoogleOAuth() throws IOException {
        HttpServletResponse response = mock(HttpServletResponse.class);
        String authUrl = "https://accounts.google.com/auth";
        when(googleOAuthService.buildAuthorizationUrl()).thenReturn(authUrl);

        googleAuthController.googleLogin(response);

        verify(response, times(1)).sendRedirect(authUrl);
    }

    @Test
    void oauth2Callback_existingMentee_shouldRedirectHome() {
        String code = "fake-code";
        String accessToken = "token";
        String email = "mentee@example.com";
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("name", "Mentee Name");
        Mentee mentee = new Mentee();
        mentee.setEmail(email);

        when(googleOAuthService.exchangeCodeForAccessToken(code)).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenReturn(userInfo);
        when(menteeRepository.findByEmail(email)).thenReturn(Optional.of(mentee));
        when(mentorRepository.findByEmail(email)).thenReturn(Optional.empty());

        String result = googleAuthController.oauth2Callback(code, session);

        assertThat(result).isEqualTo("redirect:/home");
        assertThat(session.getAttribute("loggedInUser")).isEqualTo(mentee);
        assertThat(session.getAttribute("googleUserInfo")).isEqualTo(userInfo);
    }

    @Test
    void oauth2Callback_existingMentor_shouldRedirectMentor() {
        String code = "code";
        String accessToken = "token";
        String email = "mentor@example.com";
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", email);
        Mentor mentor = new Mentor();
        mentor.setEmail(email);

        when(googleOAuthService.exchangeCodeForAccessToken(code)).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenReturn(userInfo);
        when(menteeRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(mentorRepository.findByEmail(email)).thenReturn(Optional.of(mentor));

        String result = googleAuthController.oauth2Callback(code, session);

        assertThat(result).isEqualTo("redirect:/mentor");
        assertThat(session.getAttribute("loggedInUser")).isEqualTo(mentor);
        assertThat(session.getAttribute("googleUserInfo")).isEqualTo(userInfo);
    }

    @Test
    void oauth2Callback_newUser_shouldRedirectChooseRole() {
        String code = "code";
        String accessToken = "token";
        String email = "newuser@example.com";
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", email);

        when(googleOAuthService.exchangeCodeForAccessToken(code)).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenReturn(userInfo);
        when(menteeRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(mentorRepository.findByEmail(email)).thenReturn(Optional.empty());

        String result = googleAuthController.oauth2Callback(code, session);

        assertThat(result).isEqualTo("redirect:/choose-role");
        assertThat(session.getAttribute("loggedInUser")).isNull();
    }

    @Test
    void oauth2Callback_missingEmailInUserInfo_shouldRedirectChooseRole() {
        String code = "code";
        String accessToken = "token";
        Map<String, Object> userInfo = new HashMap<>();
        // Intentionally not putting "email" in map

        when(googleOAuthService.exchangeCodeForAccessToken(code)).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenReturn(userInfo);

        // Should not throw
        String result = googleAuthController.oauth2Callback(code, session);

        assertThat(result).isEqualTo("redirect:/choose-role");
        assertThat(session.getAttribute("loggedInUser")).isNull();
    }

    @Test
    void oauth2Callback_nullUserInfo_shouldRedirectChooseRole() {
        String code = "code";
        String accessToken = "token";

        when(googleOAuthService.exchangeCodeForAccessToken(code)).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenReturn(null);

        String result = googleAuthController.oauth2Callback(code, session);

        assertThat(result).isEqualTo("redirect:/404");
        assertThat(session.getAttribute("loggedInUser")).isNull();
    }

    @Test
    void oauth2Callback_codeExchangeFails_shouldRedirectChooseRole() {
        String code = "code";
        // Simulate exchangeCodeForAccessToken returns null
        when(googleOAuthService.exchangeCodeForAccessToken(code)).thenReturn(null);

        String result = googleAuthController.oauth2Callback(code, session);

        assertThat(result).isEqualTo("redirect:/404");
        assertThat(session.getAttribute("loggedInUser")).isNull();
    }

    @Test
    void oauth2Callback_fetchUserInfoThrows_shouldRedirectChooseRole() {
        String code = "code";
        String accessToken = "token";
        when(googleOAuthService.exchangeCodeForAccessToken(code)).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenThrow(new RuntimeException("Error"));

        String result = googleAuthController.oauth2Callback(code, session);

        assertThat(result).isEqualTo("redirect:/404");
        assertThat(session.getAttribute("loggedInUser")).isNull();
    }
}
