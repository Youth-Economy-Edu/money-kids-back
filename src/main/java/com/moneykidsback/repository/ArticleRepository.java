package com.moneykidsback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.moneykidsback.model.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    // 주식과 관련된 기사 찾기
    Article findByStockId(String stockId);

    // AI 기사 등록
    Article save(Article article);
}
