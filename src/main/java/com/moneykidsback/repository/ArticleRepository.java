package com.moneykidsback.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.moneykidsback.model.entity.Article;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 주식과 관련된 기사 찾기
    Article findByStockId(String stockId);

    // 날짜별 기사 조회
    List<Article> findByDate(LocalDate date);

    // AI 기사 등록
    Article save(Article article);
}
