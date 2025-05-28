package com.moneykidsback.repository;

import com.moneykidsback.model.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    // 주식과 관련된 기사 찾기
    Article findByStockCode(String stockCode);

    // AI 기사 등록
    Article save(Article article);
}
