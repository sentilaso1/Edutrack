package com.example.edutrack.common.service.interfaces;

import com.example.edutrack.common.model.ModelConfig;
import org.springframework.http.HttpHeaders;

import java.util.Map;

public interface LLMService {
    String callModel(String prompt);
}
