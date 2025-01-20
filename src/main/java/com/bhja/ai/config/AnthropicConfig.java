package com.bhja.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai.anthropic")
@Getter
@Setter
public class AnthropicConfig {
    private String modelId;
    private int maxToken;
    private Float temperature;
    private Float permutations;
}
