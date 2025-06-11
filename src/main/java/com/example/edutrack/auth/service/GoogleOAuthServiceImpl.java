package com.example.edutrack.auth.service;

import com.example.edutrack.auth.service.interfaces.GoogleOAuthService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class GoogleOAuthServiceImpl implements GoogleOAuthService {

    public static final String CLIENT_ID = "901558962490-2apdkvt6497m31cda5u01vq6jf1f2ur9.apps.googleusercontent.com";
    public static final String CLIENT_SECRET = "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";
    public static final String REDIRECT_URI = "http://localhost:6969/oauth2/callback";
    public static final String AUTH_URI = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
    public static final String USER_INFO_URI = "https://www.googleapis.com/oauth2/v3/userinfo";
    public static final String SCOPE = "openid email profile";

    @Override
    public String buildAuthorizationUrl() {
        return AUTH_URI +
                "?client_id=" + CLIENT_ID +
                "&redirect_uri=" + REDIRECT_URI +
                "&response_type=code" +
                "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8);
    }

    @Override
    public String exchangeCodeForAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        Map<String, Object> tokenResponse = restTemplate.postForObject(
                TOKEN_URI, request, Map.class
        );
        return (String) tokenResponse.get("access_token");
    }

    @Override
    public Map<String, Object> fetchUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                USER_INFO_URI,
                HttpMethod.GET,
                entity,
                Map.class
        );

        return response.getBody();
    }
}
