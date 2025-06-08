package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.ChatCompletionRequestDto;
import com.moneykidsback.model.dto.response.ChatCompletionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final WebClient openAiWebClient;

        // 요청 본문 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        requestBody.put("temperature", 0.7); // 약간의 랜덤성 부여
    public String getChatCompletionSync(String prompt) {
        // 요청 객체 만들어서
        ChatCompletionRequestDto request = new ChatCompletionRequestDto();
        request.setModel("gpt-3.5-turbo");
        request.setMessages(List.of(new ChatCompletionRequestDto.Message("user", prompt)));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        // OpenAI API에 POST 요청
        ChatCompletionResponse response = openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .block(); // block()이 붙으면 '동기적으로' 응답을 기다려!

        // 요청 보내기
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity("https://api.openai.com/v1/chat/completions", request, Map.class);

        // 응답 파싱
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("choices")) {
            throw new RuntimeException("OpenAI 응답 형식 오류");
        }
        // 응답에서 텍스트만 추출해서 리턴
        return response.getChoices().get(0).getMessage().getContent();
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
