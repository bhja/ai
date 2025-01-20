package com.bhja.ai.controller;

import com.bhja.ai.service.IBedrockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController("/converse")
public class TextAiController {

    private final IBedrockService bedrockService;

    /**
     * Use this to test a sample titan  Q&A
     * @param request a question
     * @return response Map
     */

    @PostMapping("/titan/text")
    public ResponseEntity<Map<String, Object>> handleText(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(bedrockService.converseWithTitan(request.get("request")));
    }

    /**
     * Use this to send a text for summarization.
     * @param request Text and request
     * @return response Map
     */
    @PostMapping("/antropic/textsummary")
    public ResponseEntity<Map<String, Object>> handleTextSummary(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(bedrockService.summarizationWithAnthropicViaClaude(request.get("text"), request.get(
                "request")));
    }
}
