package com.moneykidsback.service;

import com.moneykidsback.model.entity.Article;
import com.moneykidsback.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private NewsGenerateService newsGenerateService;
    
    @Autowired
    private NewsBasedPriceService newsBasedPriceService;

    // 주식과 관련된 기사 찾기
    public Article findArticleByStockId(String stockId) {
        return articleRepository.findByStockId(stockId);
    }

    // 모든 기사 조회
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    // AI 기사 등록
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    // 정기적으로 기사를 생성하는 스케줄링 메서드 - 4시간마다 실행
    @Scheduled(cron = "0 0 0,4,8,12,16,20 * * *") // 매 4시간마다 실행
    public void generateScheduledArticles() {
        try {
            System.out.println("🗞️ [SCHEDULED] 정기 기사 생성 작업 시작");
            
            // 1. AI 기사 생성 및 저장
            List<String> generatedArticles = newsGenerateService.generateAndSaveNewsForAllStocks();
            
            if (generatedArticles != null && !generatedArticles.isEmpty()) {
                System.out.println("📰 생성된 기사 수: " + generatedArticles.size());
                
                // 2. 기사 생성 완료 후 5분 대기 (기사가 시장에 영향을 주는 시간)
                System.out.println("⏰ 기사 발표 후 시장 반응 대기 중... (5분)");
                Thread.sleep(5 * 60 * 1000); // 5분 대기
                
                // 3. 기사 기반 주가 변동 시작
                System.out.println("📈 기사 기반 주가 변동 시작");
                newsBasedPriceService.startNewsBasedPriceMovement();
                
            } else {
                System.out.println("⚠️ 기사 생성 실패 또는 빈 결과");
            }
            
        } catch (Exception e) {
            System.err.println("❌ 정기 기사 생성 작업 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
