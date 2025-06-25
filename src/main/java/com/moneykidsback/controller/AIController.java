package com.moneykidsback.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.service.NewsBasedPriceService;
import com.moneykidsback.service.NewsGenerateService;
import com.moneykidsback.service.OpenAiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 🤖 AI 뉴스 시스템 컨트롤러
 * - OpenAI API 기반 경제 뉴스 자동 생성
 * - 기사와 주가 연동 시스템
 * - 뉴스 기반 주가 변동 시뮬레이션
 * - 4시간마다 자동 생성 스케줄링
 */
@Tag(name = "AI News", description = "AI 뉴스 생성 시스템")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class AIController {

    private final OpenAiService openAiService;
    private final NewsGenerateService newsGenerateService;
    private final NewsBasedPriceService newsBasedPriceService;
    
    @Value("${openai.api.key}")
    private String configApiKey;

    @Operation(summary = "AI 기사 생성", description = "모든 주식에 대한 AI 기사를 생성합니다")
    @PostMapping("/generate")
    public List<String> generateArticleByStock() throws InterruptedException {
        return newsGenerateService.generateAndSaveNewsForAllStocks();
    }

    @Operation(summary = "API 키 테스트", description = "OpenAI API 키가 정상 작동하는지 테스트합니다")
    @GetMapping("/test-api-key")
    public String testApiKey() {
        System.out.println("=== API 키 테스트 요청 수신됨 ===");
        System.out.println("Controller에서 읽은 API 키: " + (configApiKey == null ? "null" : "[" + configApiKey.length() + "자리]"));
        System.out.println("API 키 시작부분: " + (configApiKey == null ? "null" : configApiKey.substring(0, Math.min(15, configApiKey.length())) + "..."));
        System.out.println("sk-로 시작하는가: " + (configApiKey != null && configApiKey.startsWith("sk-")));
        
        String result = openAiService.getChatCompletionSync("Hello, OpenAI!");
        System.out.println("=== API 키 테스트 결과: " + result + " ===");
        return result;
    }
    
    // 기사 기반 주가 변동 테스트
    @Operation(summary = "주가 변동 시뮬레이션", description = "기사 기반 주가 변동을 수동으로 실행합니다")
    @PostMapping("/trigger-price-movement")
    public Map<String, Object> triggerNewsBasedPriceMovement() {
        try {
            System.out.println("🚀 [MANUAL TRIGGER] 기사 기반 주가 변동 수동 실행");
            newsBasedPriceService.startNewsBasedPriceMovement();
            
            return Map.of(
                "success", true,
                "message", "기사 기반 주가 변동이 시작되었습니다. 로그를 확인해주세요.",
                "status", "STARTED"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "기사 기반 주가 변동 실행 실패: " + e.getMessage(),
                "status", "FAILED"
            );
        }
    }
    
    // 전체 워크플로우 테스트 (기사 생성 + 주가 변동)
    @Operation(summary = "전체 워크플로우 실행", description = "기사 생성과 주가 변동을 포함한 전체 워크플로우를 실행합니다")
    @PostMapping("/full-workflow")
    public Map<String, Object> runFullWorkflow() throws InterruptedException {
        try {
            System.out.println("🎯 [FULL WORKFLOW] 기사 생성 + 주가 변동 전체 워크플로우 시작");
            
            // 1. 기사 생성
            System.out.println("1️⃣ 기사 생성 중...");
            List<String> articles = newsGenerateService.generateAndSaveNewsForAllStocks();
            
            if (articles == null || articles.isEmpty()) {
                return Map.of(
                    "success", false,
                    "message", "기사 생성에 실패했습니다.",
                    "step", "ARTICLE_GENERATION_FAILED"
                );
            }
            
            // 2. 잠시 대기 (기사 발표 효과)
            System.out.println("2️⃣ 기사 발표 효과 대기 중... (30초)");
            Thread.sleep(30000); // 30초 대기
            
            // 3. 주가 변동 시작
            System.out.println("3️⃣ 기사 기반 주가 변동 시작");
            newsBasedPriceService.startNewsBasedPriceMovement();
            
            return Map.of(
                "success", true,
                "message", "전체 워크플로우가 성공적으로 시작되었습니다.",
                "articlesGenerated", articles.size(),
                "status", "WORKFLOW_STARTED"
            );
            
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "워크플로우 실행 중 오류: " + e.getMessage(),
                "status", "WORKFLOW_FAILED"
            );
        }
    }
}
