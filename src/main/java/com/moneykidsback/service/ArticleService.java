package com.moneykidsback.service;

import com.moneykidsback.model.entity.Article;
import com.moneykidsback.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    // 주식과 관련된 기사 찾기
    public Article findArticleByStockCode(String stockCode) {
        return articleRepository.findByStockCode(stockCode);
    }

    // AI 기사 등록
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }
}
