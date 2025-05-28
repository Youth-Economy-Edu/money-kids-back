package com.moneykidsback.controller;

import com.moneykidsback.model.entity.Article;
import com.moneykidsback.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    // 주식과 관련된 기사 찾기
    @GetMapping("/article/stock/{stockCode}")
    public String getArticleByStockCode(String stockCode) {
        return articleService.findArticleByStockCode(stockCode).getContent();
    }

    // AI 기사 등록
    @PostMapping("/article")
    public String saveArticle(int stockId, String title, String date, String content, String effect) {
        Article article = new Article();
        article.setStockId(stockId);
        article.setTitle(title);
        article.setDate(date);
        article.setContent(content);
        article.setEffect(effect);

        articleService.saveArticle(article);
        return "Article saved successfully!";
    }
}