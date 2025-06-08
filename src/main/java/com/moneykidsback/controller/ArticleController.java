package com.moneykidsback.controller;

import com.moneykidsback.model.entity.Article;
import com.moneykidsback.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    // 주식과 관련된 기사 찾기
    @GetMapping("/{stockCode}")
    public String getArticleByStockId(
            @PathVariable String stockId
    ) {
        return articleService.findArticleByStockId(stockId).getContent();
    }

    // AI 기사 등록
    @PostMapping()
    public ResponseEntity<Article> saveArticle(
            @RequestBody Article article
    ) {
        Article savedArticle = articleService.saveArticle(article);
        return new ResponseEntity<>(savedArticle, HttpStatus.CREATED);
    }
}