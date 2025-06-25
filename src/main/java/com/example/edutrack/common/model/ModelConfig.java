package com.example.edutrack.common.model;

public class ModelConfig {
    public final String apiKey;
    public final String model;
    public final String endpoint;
    public final ModelProvider provider;

    public ModelConfig(String apiKey, String model, String endpoint, ModelProvider provider) {
        this.apiKey = apiKey;
        this.model = model;
        this.endpoint = endpoint;
        this.provider = provider;
    }
}