package com.moneykidsback.service.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

// OpenAIClient는 LLMClient 인터페이스를 구현하여 OpenAI API를 호출하고,
// 청소년 경제 교육 분석을 위한 프롬프트를 전송하고,
// 응답으로부터 분석 결과를 JSON 형식으로 추출하는 기능 제공
@Component
@RequiredArgsConstructor
@Primary
public class OpenAIClient implements LLMClient {

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String requestAnalysis(String prompt) {
        System.out.println("🔍 OpenAIClient.requestAnalysis() 호출됨");
        System.out.println("🔑 API 키 상태: " + (openaiApiKey == null ? "null" : 
            (openaiApiKey.trim().isEmpty() ? "empty" : 
                (openaiApiKey.startsWith("sk-") ? "valid format" : "invalid format"))));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "temperature", 0.7,
                "messages", List.of(
                        Map.of("role", "system", "content", "너는 청소년 경제교육 분석가야. JSON 형식으로 분석 결과만 반환해."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            System.out.println("🌐 OpenAI API 호출 시작...");
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                request,
                String.class
        );
            System.out.println("✅ OpenAI API 호출 성공");

        // 전체 JSON 응답에서 content만 추출
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
                String content = root.at("/choices/0/message/content").asText();
                System.out.println("📝 추출된 응답: " + content);
                return content;
            } catch (Exception e) {
                System.err.println("❌ OpenAI 응답 파싱 실패: " + e.getMessage());
                throw new RuntimeException("OpenAI 응답 파싱 실패", e);
            }
        } catch (Exception e) {
            System.err.println("❌ OpenAI API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("OpenAI API 호출 실패", e);
        }
    }
}
