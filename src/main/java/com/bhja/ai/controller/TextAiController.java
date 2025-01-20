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

    @PostMapping("/titan/text")
    public ResponseEntity<String> handleText(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(bedrockService.converseWithTitan(request.get("text")));
    }

    @PostMapping("/antropic/textsummary")
    public ResponseEntity<String> handleTextSummary(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(bedrockService.summarizationWithAnthropicViaClaude(request.get("text"), request.get(
                "request")));
    }
}
