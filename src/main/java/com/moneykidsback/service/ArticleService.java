package com.moneykidsback.service;

import com.moneykidsback.model.entity.Article;
import com.moneykidsback.repository.ArticleRepository;
import com.moneykidsback.service.client.ArticleAIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    // 주식과 관련된 기사 찾기
    public Article findArticleByStockId(String stockId) {
        return articleRepository.findByStockId(stockId);
    }

    // AI 기사 등록
    @Scheduled(cron = "0 0 0,4,8,12,16,20 * * *") // 매 4시간마다 실행
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }
}
