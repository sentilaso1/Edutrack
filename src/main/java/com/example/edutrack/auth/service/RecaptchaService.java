package com.example.edutrack.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RecaptchaService {

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    public boolean verify(String responseToken, String remoteIp) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = new HashMap<>();
        ResponseEntity<Map> response = restTemplate.postForEntity(VERIFY_URL + "?secret=" + recaptchaSecret + "&response=" + responseToken + "&remoteip=" + remoteIp, null, Map.class);
        Map<String, Object> responseBody = response.getBody();

        System.out.println("reCAPTCHA response: " + body);

        return responseBody != null && Boolean.TRUE.equals(responseBody.get("success"));
    }
}
