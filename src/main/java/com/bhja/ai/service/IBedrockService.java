package com.bhja.ai.service;

import java.util.Map;

public interface IBedrockService {

    Map<String, Object> converseWithTitan(String request);

    Map<String, Object> summarizationWithAnthropicViaClaude(String request, String summarization);
}
