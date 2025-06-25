package com.moneykidsback.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.model.entity.Article;
import com.moneykidsback.service.ArticleService;
import com.moneykidsback.service.NewsGenerateService;
import com.moneykidsback.service.NewsBasedPriceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 📰 경제 소식/기사 컨트롤러
 * - AI 생성 뉴스 기사 관리
 * - 주식별 기사 조회
 * - 기사와 주가 연동 시스템
 */
@Tag(name = "Article", description = "경제 기사 관리")
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private NewsGenerateService newsGenerateService;

    @Autowired
    private NewsBasedPriceService newsBasedPriceService;

    @Operation(summary = "특정 주식의 기사 조회", description = "주식 ID로 해당 주식의 기사를 조회합니다")
    @GetMapping("/stock/{stockId}")
    public ResponseEntity<?> getArticleByStockId(
            @Parameter(description = "주식 ID", required = true) @PathVariable String stockId) {
        try {
            Article article = articleService.findArticleByStockId(stockId);
            if (article == null) {
                return ResponseEntity.ok(Map.of(
                    "code", 404,
                    "data", null,
                    "msg", "해당 주식의 기사가 없습니다."
                ));
            }

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of(
                    "id", article.getId(),
                    "stockId", article.getStockId(),
                    "title", article.getTitle(),
                    "content", article.getContent(),
                    "effect", article.getEffect(),
                    "sentiment", article.getSentiment(),
                    "impact", article.getImpact(),
                    "date", article.getDate()
                ),
                "msg", "기사 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "기사 조회 실패: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "모든 기사 조회", description = "등록된 모든 기사를 조회합니다")
    @GetMapping("/all")
    public ResponseEntity<?> getAllArticles() {
        try {
            List<Article> articles = articleService.getAllArticles();

            List<Map<String, Object>> articleData = articles.stream()
                .map(article -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", article.getId());
                    map.put("stockId", article.getStockId());
                    map.put("title", article.getTitle());
                    map.put("content", article.getContent());
                    map.put("effect", article.getEffect());
                    map.put("sentiment", article.getSentiment());
                    map.put("impact", article.getImpact());
                    map.put("date", article.getDate());
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", articleData,
                "msg", "전체 기사 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "기사 조회 실패: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "AI 기사 생성", description = "모든 주식에 대한 AI 기사를 생성합니다")
    @PostMapping("/generate")
    public ResponseEntity<?> generateArticles() {
        try {
            System.out.println("📰 기사 생성 요청 수신");
            List<String> articles = newsGenerateService.generateAndSaveNewsForAllStocks();

            newsBasedPriceService.startNewsBasedPriceMovement();

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of(
                    "generatedCount", articles.size(),
                    "articles", articles
                ),
                "msg", "기사 생성 완료"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "기사 생성 실패: " + e.getMessage()
            ));
        }
    }

    @Operation(summary = "기사 등록", description = "새로운 기사를 등록합니다")
    @PostMapping()
    public ResponseEntity<Article> saveArticle(@RequestBody Article article) {
        Article savedArticle = articleService.saveArticle(article);
        return new ResponseEntity<>(savedArticle, HttpStatus.CREATED);
    }
}
