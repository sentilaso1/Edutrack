package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.Mentee;
import com.example.edutrack.accounts.model.Mentor;
import com.example.edutrack.accounts.service.interfaces.MenteeService;
import com.example.edutrack.accounts.service.interfaces.MentorService;
import com.example.edutrack.auth.controller.GoogleAuthController;
import com.example.edutrack.auth.service.interfaces.GoogleOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthControllerTest {

    @InjectMocks
    private GoogleAuthController googleAuthController;

    @Mock
    private GoogleOAuthService googleOAuthService;

    @Mock
    private MenteeService menteeService;

    @Mock
    private MentorService mentorService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private final String dummyEmail = "test@example.com";

    @Test
    void F1_shouldRedirectToGoogleOAuthUrl() throws IOException {
        String redirectUrl = "https://accounts.google.com/o/oauth2/auth";
        when(googleOAuthService.buildAuthorizationUrl()).thenReturn(redirectUrl);

        googleAuthController.googleLogin(response);

        verify(response).sendRedirect(redirectUrl);
    }

    @Test
    void F2_shouldRedirectToHomeWhenUserIsMentee() {
        String accessToken = "validToken";
        Map<String, Object> userInfo = Map.of("email", dummyEmail);

        Mentee mentee = new Mentee(); // dummy object
        when(googleOAuthService.exchangeCodeForAccessToken("code")).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenReturn(userInfo);
        when(menteeService.findByEmail(dummyEmail)).thenReturn(Optional.of(mentee));

        String view = googleAuthController.oauth2Callback("code", session);

        verify(session).setAttribute("googleUserInfo", userInfo);
        verify(session).setAttribute("loggedInUser", mentee);
        verify(session).setAttribute("role", "mentee");
        assertEquals("redirect:/", view);
    }

    @Test
    void F3_shouldRedirectToMentorWhenUserIsMentor() {
        String accessToken = "validToken";
        Map<String, Object> userInfo = Map.of("email", dummyEmail);

        Mentor mentor = new Mentor(); // dummy object
        when(googleOAuthService.exchangeCodeForAccessToken("code")).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenReturn(userInfo);
        when(menteeService.findByEmail(dummyEmail)).thenReturn(Optional.empty());
        when(mentorService.findByEmail(dummyEmail)).thenReturn(Optional.of(mentor));

        String view = googleAuthController.oauth2Callback("code", session);

        verify(session).setAttribute("googleUserInfo", userInfo);
        verify(session).setAttribute("loggedInUser", mentor);
        verify(session).setAttribute("role", "mentor");
        assertEquals("redirect:/mentor", view);
    }

    @Test
    void F4_shouldRedirectToChooseRoleWhenEmailNotFound() {
        String accessToken = "validToken";
        Map<String, Object> userInfo = Map.of("email", dummyEmail);

        when(googleOAuthService.exchangeCodeForAccessToken("code")).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenReturn(userInfo);
        when(menteeService.findByEmail(dummyEmail)).thenReturn(Optional.empty());
        when(mentorService.findByEmail(dummyEmail)).thenReturn(Optional.empty());

        String view = googleAuthController.oauth2Callback("code", session);

        verify(session).setAttribute("googleUserInfo", userInfo);
        verify(session, never()).setAttribute(eq("loggedInUser"), any());
        verify(session, never()).setAttribute(eq("role"), any());
        assertEquals("redirect:/choose-role", view);
    }

    @Test
    void F5_shouldRedirectTo404WhenEmailIsMissing() {
        String accessToken = "validToken";
        Map<String, Object> userInfo = new HashMap<>();

        when(googleOAuthService.exchangeCodeForAccessToken("code")).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenReturn(userInfo);

        String view = googleAuthController.oauth2Callback("code", session);

        assertEquals("redirect:/404", view);
    }

    @Test
    void F6_shouldRedirectTo404WhenUserInfoIsNull() {
        String accessToken = "validToken";

        when(googleOAuthService.exchangeCodeForAccessToken("code")).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenReturn(null);

        String view = googleAuthController.oauth2Callback("code", session);

        assertEquals("redirect:/404", view);
    }

    @Test
    void F7_shouldRedirectTo404WhenAccessTokenIsNull() {
        when(googleOAuthService.exchangeCodeForAccessToken("code")).thenReturn(null);

        String view = googleAuthController.oauth2Callback("code", session);

        assertEquals("redirect:/404", view);
    }

    @Test
    void F8_shouldRedirectTo404WhenFetchUserInfoThrows() {
        String accessToken = "validToken";

        when(googleOAuthService.exchangeCodeForAccessToken("code")).thenReturn(accessToken);
        when(googleOAuthService.fetchUserInfo(accessToken)).thenThrow(new RuntimeException("fail"));

        String view = googleAuthController.oauth2Callback("code", session);

        assertEquals("redirect:/404", view);
    }
}

