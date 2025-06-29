package com.example.edutrack.common.service.implementations;

import com.example.edutrack.common.model.ModelConfig;
import com.example.edutrack.common.model.ModelProvider;
import com.example.edutrack.common.service.interfaces.LLMService;
import com.fasterxml.jackson.databind.JsonNode;
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
    private static final String GROQ_API_KEY = "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";
    private static final String TOGETHERAI_API_KEY = "253a683b1a6dff88b0e141d72f37ed7a80d700a4f30a545d00131fc3a591a2ca";

    private static final String TA_DEEPSEEK_MODEL = "meta-llama/Llama-3.3-70B-Instruct-Turbo-Free";
    private static final String OR_MISTRAL_MODEL = "mistralai/devstral-small:free";
    private static final String GR_COMPOUND = "compound-beta-mini";
    private static final String OPENROUTER_ENDPOINT = "https://openrouter.ai/api/v1/chat/completions";
    private static final String TOGETHERAI_ENDPOINT = "https://api.together.xyz/v1/chat/completions";
    private static final String GROQ_ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";


    private final List<ModelConfig> configs = List.of(

            new ModelConfig(TOGETHERAI_API_KEY, TA_DEEPSEEK_MODEL, TOGETHERAI_ENDPOINT, ModelProvider.TOGETHERAI),

            new ModelConfig(GROQ_API_KEY, GR_COMPOUND, GROQ_ENDPOINT, ModelProvider.GROQ),

            new ModelConfig(OPENROUTER_API_KEY_1, OR_MISTRAL_MODEL, OPENROUTER_ENDPOINT , ModelProvider.OPENROUTER),
            new ModelConfig(OPENROUTER_API_KEY_2, OR_MISTRAL_MODEL, OPENROUTER_ENDPOINT , ModelProvider.OPENROUTER),
            new ModelConfig(OPENROUTER_API_KEY_3, OR_MISTRAL_MODEL, OPENROUTER_ENDPOINT , ModelProvider.OPENROUTER)

    );

    @Override
    public String callModel (String prompt) {
        for (ModelConfig cfg : configs) {
            try {
                log.info("Attempting call to provider: {} | model: {}", cfg.provider, cfg.model);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> body;

                if (cfg.provider == ModelProvider.OPENROUTER
                        || cfg.provider == ModelProvider.GROQ
                        || cfg.provider == ModelProvider.TOGETHERAI) {

                    headers.setBearerAuth(cfg.apiKey);

                    body = new HashMap<>();
                    body.put("model", cfg.model);
                    body.put("messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    ));

                    String aiResult = tryRequest(cfg, headers, body);
                    log.info("AI Result: {}", aiResult);

                    if (aiResult == null || aiResult.trim().isEmpty()) {
                        log.warn("Provider {} returned empty result. Trying next model...", cfg.provider);
                        continue;
                    }
                    return aiResult;

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
        return "";
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
            log.info("Successful response from provider: {} | Body: {}", cfg.provider, response.getBody());
            return response.getBody();
        } else {
            log.warn("Non-2xx response from provider: {} | status: {}", cfg.provider, response.getStatusCode());
            throw new HttpClientErrorException(response.getStatusCode(), "Non-2xx status");
        }
    }
}
