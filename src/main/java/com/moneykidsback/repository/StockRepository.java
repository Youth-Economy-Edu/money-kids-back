package com.moneykidsback.repository;

import com.moneykidsback.model.dto.request.RateForAiNewsDto;
import com.moneykidsback.model.dto.response.StockChangeRateDto;
import com.moneykidsback.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    // 주식코드로 종목 찾기
    List<Stock> findByCode(String code);

    // 종목 이름으로 종목 찾기
    List<Stock> findByName(String name);

    // 카테고리로 종목 찾기
    List<Stock> findByCategory(String category);
    Stock findByID(String id);

    // 가격 기준 주식 내림차순(순위) 메소드
    List<Stock> findAllByOrderByPriceDesc();

    // 카테고리별로 주식 가격 내림차순으로 정렬된 리스트 반환
    List<Stock> findAllByCategoryOrderByPriceDesc(String category);

    // 변동률을 StockChangeRateDto 형태로 반환 (StockPriceLog의 volatility 사용)
    @Query("SELECT new com.moneykidsback.model.dto.response.StockChangeRateDto(" +
            "s.ID, s.name, s.price, s.category, s.update_at, spl.volatility) " +
            "FROM Stock s JOIN StockPriceLog spl ON s = spl.stock " +
            "WHERE spl.date = (SELECT MAX(spl2.date) FROM StockPriceLog spl2 WHERE spl2.stock = s) " +
            "ORDER BY spl.volatility DESC")
    List<StockChangeRateDto> findAllOrderByChangeRateDescWithDto();

    // 변동률을 StockChangeRateDto 형태로 반환 (StockPriceLog의 volatility 사용)
    @Query("SELECT new com.moneykidsback.model.dto.request.RateForAiNewsDto(" +
            "s.ID, " +
            "s.name, " +
            "s.category, " +
            "spl.volatility) " +
            "FROM Stock s JOIN StockPriceLog spl ON s = spl.stock " +
            "WHERE spl.date = (SELECT MAX(spl2.date) FROM StockPriceLog spl2 WHERE spl2.stock = s) " +
            "ORDER BY spl.volatility DESC")
    List<RateForAiNewsDto> findChangeRateForAiNews();


}