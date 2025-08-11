package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OPENAI_API_KEY = "YOUR_OPENAI_KEY";
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public String askOpenAi(String userInput) {
        try {
            Map<String, String> systemMsg = Map.of(
                    "role", "system",
                    "content", String.join("\n",
                            "너는 대한민국 아파트 청약 제도에 대해 깊은 전문 지식을 가진 챗봇이야.",
                            "초보자부터 전문가까지 누구든 쉽게 이해할 수 있도록 부드럽고 친절하게 설명해줘.",
                            "청약 가점제, 특별공급, 무주택 기간, 청약 통장 요건, 주택 유형, 1·2순위 조건 등 제도 전반에 대해 잘 알고 있어야 해.",
                            "모든 답변은 어려운 용어를 풀어주며, 필요하다면 예시를 들어 쉽게 설명해줘.",
                            "말투는 상담원이 고객에게 설명하듯 자연스럽고 부드럽게 유지해.",
                            "질문이 불명확할 경우 '좀 더 자세히 말씀해주시면 정확하게 안내드릴 수 있어요.'처럼 정중하게 요청해줘.",
                            "목표는 청약 제도를 처음 접하는 사용자도 안심하고 이해할 수 있도록 돕는 것이야.",
                            "청약 정보는 실제 제도 기준에 따라 정확하게 안내하고, 최신 정보를 기준으로 설명해.",
                            "설명이 너무 길어지지 않게 모든 대답은 100자 길면 150자 이내로 대답해줘"
                    )
            );
            Map<String, String> userMsg = Map.of("role", "user", "content", userInput);

            //message body 만들기 모든 message 앞에는 위에 적은 프롬프트 message를 system에게 보내게 됨
            List<Map<String, String>> messages = List.of(systemMsg, userMsg);
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o");
            body.put("messages", messages);

            //message header 만들기
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(OPENAI_API_KEY);

            // message의 body와 header 를 묶은 Entity 생성
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            //Entity를 이용해서 POST 요청 보내기 GPT 응답을 자바의 Map 으로 받음(json 형식으로)
            ResponseEntity<Map> response = restTemplate.exchange(OPENAI_URL, HttpMethod.POST, request, Map.class);

            //is2xxSuccessful 에서 2xx 는 200번응답(성공응답)을 의미함
            //choices = [
            //  {
            //    "index": 0,
            //    "message": {
            //      "role": "assistant",
            //      "content": "청약은 이런 거예요~"
            //    },
            //    "finish_reason": "stop"
            //  }
            //]
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return message.get("content").toString().trim();
            } else {
                log.error("OpenAI 응답 실패: {}", response);
                return "OpenAI 호출 실패했습니다.";
            }

        } catch (Exception e) {
            log.error("GPT 호출 중 예외", e);
            return "GPT 응답 중 오류가 발생했습니다.";
        }
    }
}
