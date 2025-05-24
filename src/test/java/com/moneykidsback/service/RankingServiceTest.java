package com.moneykidsback.service;

import com.moneykidsback.dto.request.StockInfoDto;
import com.moneykidsback.model.Stock;
import com.moneykidsback.repository.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
//@ActiveProfiles("test")
public class RankingServiceTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    @DisplayName("주식순위 쿼리")
    public void stockRankedTest() {
        // 더미 데이터 저장 (테스트 환경에서만)
        for (int i = 1; i <= 10; i++) {
            Stock stock = new Stock();
            stock.setCode("CODE" + i);
            stock.setName("테스트 스톡" + i);
            stock.setPrice(1000 * i);
            stock.setCategory("Tech");
            stockRepository.save(stock);
        }

        // 가격 내림차순으로 조회
        List<Stock> rankedStocks = stockRepository.findAllByOrderByPriceDesc();

        // 결과 출력
        for (Stock stock : rankedStocks) {
            System.out.println(stock.getCode() + " | " + stock.getName() + " | " + stock.getPrice() + " | " + stock.getCategory());
        }
    }
}

