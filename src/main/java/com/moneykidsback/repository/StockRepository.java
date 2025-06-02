package com.moneykidsback.repository;

import com.moneykidsback.model.dto.request.RateForAiNewsDto;
import com.moneykidsback.model.dto.response.StockChangeRateDto;
import com.moneykidsback.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {


    Stock findByID(int id);

    // 가격 기준 주식 내림차순(순위) 메소드
    List<Stock> findAllByOrderByPriceDesc();

    // 카테고리별로 주식 가격 내림차순으로 정렬된 리스트 반환
    List<Stock> findAllByCategoryOrderByPriceDesc(String category);

    // todo: 주식 테이블에 이전가격 컬럼 추가되면 사용가능
    // 변동률을 StockChangeRateDto 형태로 반환
    @Query("SELECT new com.moneykidsback.model.dto.response.StockChangeRateDto(" +
            "s.ID, s.name, s.price, s.category, s.updatedAt, " +
            "CASE WHEN s.beforePrice = 0 THEN 0 ELSE ((s.price - s.beforePrice) * 100.0 / s.beforePrice) END) " +
            "FROM Stock s ORDER BY " +
            "CASE WHEN s.beforePrice = 0 THEN 0 ELSE ((s.price - s.beforePrice) * 100.0 / s.beforePrice) END DESC")
    List<StockChangeRateDto> findAllOrderByChangeRateDescWithDto();


    // 변동률을 StockChangeRateDto 형태로 반환
    @Query("SELECT new com.moneykidsback.model.dto.request.RateForAiNewsDto(" +
            "s.ID, " +
            "s.name, " +
            "s.category, " +   // ★ category 추가!!
            "CASE WHEN s.beforePrice = 0 THEN 0 ELSE ((s.price - s.beforePrice) * 100.0 / s.beforePrice) END) " +
            "FROM Stock s " +
            "ORDER BY CASE WHEN s.beforePrice = 0 THEN 0 ELSE ((s.price - s.beforePrice) * 100.0 / s.beforePrice) END DESC")
    List<RateForAiNewsDto> findChangeRateForAiNews();


}
