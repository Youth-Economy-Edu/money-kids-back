package com.moneykidsback.repository;

import com.moneykidsback.dto.request.StockChangeRateDto;
import com.moneykidsback.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

    //JPA 가 네이밍 규칙 따라 자동 생성한 쿼리메소드
    List<Stock> findAllByOrderByPriceDesc(); //가격 기준 주식 내림차순(순위) 메소드

    List<Stock> findAllByCategoryOrderByPriceDesc(String category);

    // todo: 주식 테이블에 이전가격 컬럼 추가되면 사용가능
    // 변동률을 StockChangeRateDto 의 changeRate 변수에 직접 할당하는 메소드
    @Query("SELECT new com.moneykidsback.dto.request.StockChangeRateDto(" +
            "s.code, s.name, s.price, s.category, s.updatedAt, " +
            "((s.price - s.beforePrice) * 100.0 / s.beforePrice)) " +
            "FROM Stock s ORDER BY ((s.price - s.beforePrice) * 100.0 / s.beforePrice) DESC")
    List<StockChangeRateDto> findAllOrderByChangeRateDescWithDto();
}
