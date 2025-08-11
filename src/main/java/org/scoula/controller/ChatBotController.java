package org.scoula.controller;

import lombok.RequiredArgsConstructor;
import org.scoula.dto.ChatRequest;
import org.scoula.dto.ChatResponse;
import org.scoula.service.FaqService;
import org.scoula.service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/chatbot")
@RequiredArgsConstructor
public class ChatBotController {

    private final FaqService faqService;
    private final OpenAiService openAiService;

    @PostMapping
    public ResponseEntity<?> handleChat(@RequestBody ChatRequest request) {
        String userInput = request.getMessage();

        // 1. FAQ 매칭
        String faqAnswer = faqService.getFaqAnswer(userInput);
        if (faqAnswer != null) {
            return ResponseEntity.ok(new ChatResponse(faqAnswer));
        }
        return ResponseEntity.ok(Map.of("response","유료결제가 필요합니다"));


//        // 2. OpenAI 호출
//        String aiAnswer = openAiService.askOpenAi(userInput);
//        return ResponseEntity.ok(new ChatResponse(aiAnswer));
      }
    }
