package com.moneykidsback.repository;

import com.moneykidsback.model.entity.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class StockRepositoryTest {

    @Autowired
    StockRepository stockRepository;

    @Test
    @DisplayName("종목 코드로 주식 찾기")
    public void findStockByCodeTest() {
        // 1. testdb에 더미 데이터 삽입
        Stock stock1 = new Stock();
        stock1.setCode("005930");
        stock1.setName("삼성전자");
        stock1.setPrice(70000);
        stock1.setCategory("IT");
        stockRepository.save(stock1);

        Stock stock2 = new Stock();
        stock2.setCode("000660");
        stock2.setName("SK하이닉스");
        stock2.setPrice(120000);
        stock2.setCategory("IT");
        stockRepository.save(stock2);

        // 2. 주식 코드로 주식 찾기
        Stock foundStock = stockRepository.findById("005930").orElse(null);

        // 3. 결과 검증
        assert foundStock != null;
        assert "삼성전자".equals(foundStock.getName());
        assert 70000 == foundStock.getPrice();
        assert "IT".equals(foundStock.getCategory());
        assert "005930".equals(foundStock.getCode());
        System.out.println(foundStock);

        // 4. 더미 데이터 삭제 (테스트 후 정리)
        stockRepository.delete(stock1);
        stockRepository.delete(stock2);
        System.out.println("더미 데이터 삭제 완료");
    }
}