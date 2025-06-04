package com.moneykidsback.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    // OpenAI에게 현재 가격 기반 주가를 물어보고, 숫자 추출
    public int getRandomPriceFromOpenAi(int currentPrice) {
        
        // 사용자 프롬프트 구성
        String prompt = String.format(
            "현재 주가가 %d원인 주식이 있어. 오늘의 주가를 -30%% ~ +30%% 사이에서 랜덤하게 정해줘. "
            + "단, 가격은 절대로 0원 이하가 되어선 안 돼. 숫자 하나만 줘. 예: %d",
            currentPrice, (int)(currentPrice * 0.9));

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 요청 본문 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("temperature", 0.7); // 약간의 랜덤성 부여

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity("https://api.openai.com/v1/chat/completions", request, Map.class);

        // 응답 파싱
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("choices")) {
            throw new RuntimeException("OpenAI 응답 형식 오류");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = message.get("content").toString();

        // 숫자만 추출
        int newPrice = extractNumber(content);

        // 변동률 계산 및 출력(변동 알람, 주가 10퍼센트 변동시 출력)
        double percentageChange = ((double)(newPrice - currentPrice) / currentPrice) * 100;
        System.out.printf("주가 변동률: %.2f%% (기존: %d원 → 새 가격: %d원)%n", percentageChange, currentPrice, newPrice);

        return newPrice;
    }

    // 응답 문자열에서 첫 번째 숫자 추출
    private int extractNumber(String content) {
        Matcher matcher = Pattern.compile("\\d+").matcher(content);
        return matcher.find() ? Integer.parseInt(matcher.group()) : -1;
    }
}
