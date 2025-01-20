package com.bhja.ai.service;

public interface IBedrockService {

    String converseWithTitan(String request);

    String summarizationWithAnthropicViaClaude(String request, String summarization);
}
