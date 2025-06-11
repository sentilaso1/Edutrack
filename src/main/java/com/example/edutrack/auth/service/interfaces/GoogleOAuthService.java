package com.example.edutrack.auth.service.interfaces;

import java.util.Map;

public interface GoogleOAuthService {
    String buildAuthorizationUrl();
    String exchangeCodeForAccessToken(String code);
    Map<String, Object> fetchUserInfo(String accessToken);
}