package com.example.edutrack.common.service.implementations;

import com.example.edutrack.common.model.ModelConfig;
import com.example.edutrack.common.model.ModelProvider;
import com.example.edutrack.common.service.interfaces.LLMService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LLMServiceImpl implements LLMService {

    private static final Logger log = LoggerFactory.getLogger(LLMService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OPENROUTER_API_KEY_1 = "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";
    private static final String OPENROUTER_API_KEY_2 = "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";
    private static final String OPENROUTER_API_KEY_3 = "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";
    private static final String NOVITA_API_KEY = "sk_wDNGmRzFb-yIoJ0fsmtuYssSKB3D_5GlgQ2x3bTsZ3c";

    private static final String DEEPSEEK_MODEL = "deepseek/deepseek-chat-v3-0324:free";
    private static final String MISTRAL_MODEL = "mistralai/devstral-small:free";
    private static final String GEMINI_MODEL = "google/gemini-2.0-flash-exp:free";
    private static final String OPENROUTER_ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";


    private final List<ModelConfig> configs = List.of(

            // Deepseek
            new ModelConfig(OPENROUTER_API_KEY_1, DEEPSEEK_MODEL, OPENROUTER_ENDPOINT , ModelProvider.DEEPSEEK),
            new ModelConfig(OPENROUTER_API_KEY_2, DEEPSEEK_MODEL, OPENROUTER_ENDPOINT , ModelProvider.DEEPSEEK),
            new ModelConfig(OPENROUTER_API_KEY_3, DEEPSEEK_MODEL, OPENROUTER_ENDPOINT , ModelProvider.DEEPSEEK),

            // Mistral
            new ModelConfig(OPENROUTER_API_KEY_1, MISTRAL_MODEL, OPENROUTER_ENDPOINT, ModelProvider.MISTRAL),
            new ModelConfig(OPENROUTER_API_KEY_2, MISTRAL_MODEL, OPENROUTER_ENDPOINT, ModelProvider.MISTRAL),
            new ModelConfig(OPENROUTER_API_KEY_3, MISTRAL_MODEL, OPENROUTER_ENDPOINT, ModelProvider.MISTRAL),

            // Gemini
            new ModelConfig(OPENROUTER_API_KEY_1, GEMINI_MODEL, OPENROUTER_ENDPOINT, ModelProvider.GEMINI),
            new ModelConfig(OPENROUTER_API_KEY_2, GEMINI_MODEL, OPENROUTER_ENDPOINT, ModelProvider.GEMINI),
            new ModelConfig(OPENROUTER_API_KEY_3, GEMINI_MODEL, OPENROUTER_ENDPOINT, ModelProvider.GEMINI)


    );

    @Override
    public String callModel (String prompt) {
        for (ModelConfig cfg : configs) {
            try {
                log.info("Attempting call to provider: {} | model: {}", cfg.provider, cfg.model);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> body;

                if (cfg.provider == ModelProvider.DEEPSEEK
                        || cfg.provider == ModelProvider.MISTRAL
                        || cfg.provider == ModelProvider.GEMINI) {

                    headers.setBearerAuth(cfg.apiKey);

                    body = new HashMap<>();
                    body.put("model", cfg.model);
                    body.put("messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    ));

                    return tryRequest(cfg, headers, body);

                } else {
                    throw new IllegalStateException("Unknown model provider: " + cfg.provider);
                }

            } catch (HttpClientErrorException | HttpServerErrorException ex) {
                log.error("HTTP error from provider: {} | model: {} | status: {} | body: {}",
                        cfg.provider, cfg.model, ex.getStatusCode(), ex.getResponseBodyAsString());
            } catch (Exception ex) {
                log.error("Exception from provider: {} | model: {} | message: {}",
                        cfg.provider, cfg.model, ex.getMessage(), ex);
            }
        }
        log.error("All model providers and API keys failed for prompt: {}", prompt);
        throw new RuntimeException("All model providers and API keys failed.");
    }

    private String tryRequest(ModelConfig cfg, HttpHeaders headers, Map<String, Object> body) throws Exception {
        String bodyJson = objectMapper.writeValueAsString(body);
        log.debug("Sending request to {} with body: {}", cfg.endpoint, bodyJson);

        HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                cfg.endpoint,
                entity,
                String.class
        );

        log.info("Received HTTP status {} from provider: {}", response.getStatusCode(), cfg.provider);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Successful response from provider: {}", cfg.provider);
            return response.getBody();
        } else {
            log.warn("Non-2xx response from provider: {} | status: {}", cfg.provider, response.getStatusCode());
            throw new HttpClientErrorException(response.getStatusCode(), "Non-2xx status");
        }
    }
}
