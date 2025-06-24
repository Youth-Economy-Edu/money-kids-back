package com.moneykidsback.repository;

import com.moneykidsback.model.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    
    // 주식 ID로 기사 조회
    Article findByStockId(String stockId);
    
    // 주식 ID로 기사 목록 조회
    List<Article> findAllByStockId(String stockId);
    
    // 날짜 범위로 기사 조회 (String 타입으로 변경)
    @Query("SELECT a FROM Article a WHERE a.date >= :startDate AND a.date <= :endDate")
    List<Article> findByDateBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    // 오늘 생성된 기사 조회 (String 타입으로 변경)
    @Query("SELECT a FROM Article a WHERE a.date >= :date")
    List<Article> findByDateAfter(@Param("date") String date);
    
    // 감정별 기사 조회
    List<Article> findBySentiment(String sentiment);
    
    // 영향도별 기사 조회
    List<Article> findByImpact(String impact);
    
    // 최신 기사 N개 조회 (수정됨 - @Query 어노테이션 사용)
    @Query("SELECT a FROM Article a ORDER BY a.date DESC LIMIT :limit")
    List<Article> findTopNByOrderByDateDesc(@Param("limit") int limit);
    
    // 특정 주식의 최신 기사 조회
    @Query("SELECT a FROM Article a WHERE a.stockId = :stockId ORDER BY a.date DESC LIMIT 1")
    Article findTopByStockIdOrderByDateDesc(@Param("stockId") String stockId);
    
    // 활성 상태의 기사들 조회 (영향력이 아직 남아있는) - String 타입으로 변경
    @Query("SELECT a FROM Article a WHERE a.date >= :cutoffTime ORDER BY a.date DESC")
    List<Article> findActiveArticles(@Param("cutoffTime") String cutoffTime);
    
    // 강한 영향력의 기사들 조회 - String 타입으로 변경
    @Query("SELECT a FROM Article a WHERE a.impact = 'STRONG' AND a.date >= :cutoffTime ORDER BY a.date DESC")
    List<Article> findStrongImpactArticles(@Param("cutoffTime") String cutoffTime);
}
