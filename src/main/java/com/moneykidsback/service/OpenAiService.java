package com.moneykidsback.service;

import lombok.RequiredArgsConstructor;
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

    @jakarta.annotation.PostConstruct
    private void debugApiKey() {
        System.out.println("=== OpenAI API 키 디버깅 정보 ===");
        System.out.println("API 키 원본: " + (apiKey == null ? "null" : "[" + apiKey.length() + "자리]"));
        if (apiKey != null && apiKey.length() > 20) {
            System.out.println("API 키 시작부분: " + apiKey.substring(0, 20) + "...");
            System.out.println("API 키 끝부분: ..." + apiKey.substring(apiKey.length() - 10));
        } else {
            System.out.println("API 키 전체: " + apiKey);
        }
        System.out.println("공백 포함 여부: " + (apiKey != null && apiKey.contains(" ")));
        System.out.println("null 여부: " + (apiKey == null));
        System.out.println("empty 여부: " + (apiKey != null && apiKey.trim().isEmpty()));
        System.out.println("sk-로 시작하는가: " + (apiKey != null && apiKey.startsWith("sk-")));
        System.out.println("dummy와 같은가: " + (apiKey != null && apiKey.equals("dummy")));
        System.out.println("유효성 검사 결과: " + (isInvalidApiKey(apiKey) ? "❌ 유효하지 않음" : "✅ 유효함"));
        
        // 시스템 환경변수도 확인
        String envKey = System.getenv("OPENAI_API_KEY");
        System.out.println("시스템 환경변수 OPENAI_API_KEY: " + (envKey == null ? "null" : "[" + envKey.length() + "자리]"));
        System.out.println("==========================");
    }

    public String getChatCompletionSync(String prompt) {
        // API 키가 비어있거나 더미 키인 경우 기본 응답 반환
        if (isInvalidApiKey(apiKey)) {
            System.out.println("OpenAI API 키가 설정되지 않았습니다. 기본 응답을 반환합니다.");
            return "기본 응답 - OpenAI API 키를 설정해주세요";
        }
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("messages", List.of(Map.of("role", "user", "content", prompt)));
            requestBody.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", 
                request, 
                Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            
            // 🆕 OpenAI 응답 로깅 추가
            System.out.println("=== OpenAI API Raw Response ===");
            System.out.println("Raw Response = " + responseBody);
            System.out.println("===============================");
            
            if (responseBody == null || !responseBody.containsKey("choices")) {
                throw new RuntimeException("OpenAI 응답 형식 오류");
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = message.get("content").toString();
            
            // 🆕 추출된 콘텐츠도 로깅
            System.out.println("=== OpenAI Extracted Content ===");
            System.out.println("Content = " + content);
            System.out.println("===============================");
            
            return content;
            
        } catch (Exception e) {
            System.err.println("OpenAI API 호출 실패: " + e.getMessage());
            e.printStackTrace(); // 🆕 스택 트레이스도 출력
            return "랜덤 응답"; // 실패시 기본 응답
        }
    }

    public int getRandomPriceFromOpenAi(int currentPrice) {
        // API 키가 비어있거나 더미 키인 경우 랜덤 변동 적용
        if (isInvalidApiKey(apiKey)) {
            System.out.println("OpenAI API 키가 설정되지 않았습니다. 랜덤 변동을 적용합니다.");
            Random random = new Random();
            double changeRate = (random.nextDouble() - 0.5) * 0.1; // -5% ~ +5%
            int randomPrice = (int) (currentPrice * (1 + changeRate));
            System.out.println("[FALLBACK] 랜덤 가격 생성: " + currentPrice + " → " + randomPrice + " (변동률: " + String.format("%.2f", changeRate * 100) + "%)");
            return randomPrice;
        }
        
        try {
            String prompt = String.format(
                "현재 주가가 %d원입니다. 실제 주식 시장처럼 약간의 변동을 주어 새로운 주가를 제안해주세요. " +
                "±5%% 범위 내에서 자연스러운 변동을 적용하되, 정수로만 답변해주세요.", 
                currentPrice
            );
            
            System.out.println("[OpenAI] 주가 변동 요청 중... 현재가: " + currentPrice + "원");
            String response = getChatCompletionSync(prompt);
            System.out.println("[OpenAI] 원본 응답: " + response);
            
            // 응답에서 숫자만 추출
            int newPrice = extractNumber(response);
            System.out.println("[OpenAI] 추출된 가격: " + newPrice + "원");
            
            // 유효하지 않은 가격이면 현재 가격 기준으로 약간의 랜덤 변동 적용
            if (newPrice <= 0 || Math.abs(newPrice - currentPrice) > currentPrice * 0.1) {
                System.out.println("[OpenAI] 유효하지 않은 가격, 랜덤 fallback 적용");
                Random random = new Random();
                double changeRate = (random.nextDouble() - 0.5) * 0.1; // -5% ~ +5%
                newPrice = (int) (currentPrice * (1 + changeRate));
                System.out.println("[FALLBACK] 랜덤 가격: " + newPrice + "원");
            } else {
                double changeRate = (newPrice - currentPrice) * 100.0 / currentPrice;
                System.out.println("[OpenAI SUCCESS] " + currentPrice + " → " + newPrice + " (변동률: " + String.format("%.2f", changeRate) + "%)");
            }
            
            return newPrice;
        } catch (Exception e) {
            System.err.println("OpenAI API 호출 실패, 랜덤 가격 생성: " + e.getMessage());
            // API 호출 실패시 랜덤 변동 적용
            Random random = new Random();
            double changeRate = (random.nextDouble() - 0.5) * 0.1; // -5% ~ +5%
            int fallbackPrice = (int) (currentPrice * (1 + changeRate));
            System.out.println("[ERROR FALLBACK] " + currentPrice + " → " + fallbackPrice + " (변동률: " + String.format("%.2f", changeRate * 100) + "%)");
            return fallbackPrice;
        }
    }

    // 응답 문자열에서 첫 번째 숫자 추출
    private int extractNumber(String content) {
        Matcher matcher = Pattern.compile("\\d+").matcher(content);
        return matcher.find() ? Integer.parseInt(matcher.group()) : -1;
    }

    private boolean isInvalidApiKey(String key) {
        return key == null || key.trim().isEmpty() || key.equals("dummy") || !key.startsWith("sk-");
    }
}
