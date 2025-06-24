package com.moneykidsback.service;

import com.moneykidsback.model.dto.response.StockChangeRateDto;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
//@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
//@ActiveProfiles("test")
public class RankingServiceTest {

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void setUp() {
        stockRepository.deleteAll(); // 테스트 전 데이터 초기화
    }

    @Test
    @DisplayName("주식순위 검색")
    public void stockRankedTest() {
        // 더미 데이터 저장 (테스트 환경에서만)
        for (int i = 1; i <= 10; i++) {
            Stock stock = new Stock();
            stock.setCode("CODE" + i);
            stock.setName("테스트 스톡" + i);
            stock.setPrice(1000 * i);
            stock.setCategory("Tech");
            stock.setUpdateAt(LocalDateTime.now());
            stockRepository.save(stock);
        }

        // 가격 내림차순으로 조회
        List<Stock> rankedStocks = stockRepository.findAllByOrderByPriceDesc();

        // 결과 출력
        for (Stock stock : rankedStocks) {
            System.out.println(stock.getCode() + " | " + stock.getName() + " | " + stock.getPrice() + " | " + stock.getCategory());
        }
    }

    @Test
    @DisplayName("카테고리별 순위 검색")
    public void stockCategoryTest() {
        for (int i = 1; i <= 5; i++) {
            Stock stock = new Stock();
            stock.setCode("CODE 1-" + i);
            stock.setName("테스트 스톡 1-" + i);
            stock.setPrice(1000 * i);
            stock.setCategory("Tech");
            stock.setUpdateAt(LocalDateTime.now());
            stockRepository.save(stock);
        }
        for (int i = 1; i <= 5; i++) {
            Stock stock2 = new Stock();
            stock2.setCode("CODE 2-" + i);
            stock2.setName("테스트 스톡 1-" + i);
            stock2.setPrice(1000 * i);
            stock2.setCategory("Defense");
            stock2.setUpdateAt(LocalDateTime.now());
            stockRepository.save(stock2);
        }

        //Defense 카테고리 검색
        List<Stock> categoryRankedStocks = stockRepository.findAllByCategoryOrderByPriceDesc("Defense");
        for (Stock stock : categoryRankedStocks) {
            System.out.println(stock.getCode() + " | " + stock.getName() + " | " + stock.getPrice() + " | " + stock.getCategory()  + " | " + stock.getUpdateAt());
        }
    }

    @Test
    @DisplayName("변동률 기준 순위 검색")
    public void stockChangeRateTest() {
            // 이전 데이터(과거 시점)
            Stock stockOld = new Stock();
            stockOld.setCode("CODE1");
            stockOld.setName("테스트 스톡1");
            stockOld.setPrice(1000); // 과거 가격
            stockOld.setBeforePrice(1000); // 최초 등록이므로 beforePrice도 동일
            stockOld.setCategory("Tech");
            stockOld.setUpdateAt(LocalDateTime.now().minusDays(1));
            stockRepository.save(stockOld);

            // 최신 데이터(현재 시점)
            Stock stockNew = new Stock();
            stockNew.setCode("CODE1");
            stockNew.setName("테스트 스톡1");
            stockNew.setPrice(1200); // 현재 가격
            stockNew.setBeforePrice(1000); // 과거 가격
            stockNew.setCategory("Tech");
            stockNew.setUpdateAt(LocalDateTime.now());
            stockRepository.save(stockNew);

            // 이전 데이터(과거 시점)
            Stock stockOld2 = new Stock();
            stockOld2.setCode("CODE2");
            stockOld2.setName("테스트 스톡2");
            stockOld2.setPrice(1000); // 과거 가격
            stockOld2.setBeforePrice(1000); // 최초 등록이므로 beforePrice도 동일
            stockOld2.setCategory("Tech");
            stockOld2.setUpdateAt(LocalDateTime.now().minusDays(1));
            stockRepository.save(stockOld2);

            // 최신 데이터(현재 시점)
            Stock stockNew2 = new Stock();
            stockNew2.setCode("CODE2");
            stockNew2.setName("테스트 스톡2");
            stockNew2.setPrice(9000); // 현재 가격
            stockNew2.setBeforePrice(1000); // 과거 가격
            stockNew2.setCategory("Tech");
            stockNew2.setUpdateAt(LocalDateTime.now());
            stockRepository.save(stockNew2);

        // 변동률 기준 순위 조회
        List<StockChangeRateDto> changeRateRankedStocks = stockRepository.findAllOrderByChangeRateDescWithDto();

        // 결과 출력
        for (StockChangeRateDto stock : changeRateRankedStocks) {
            System.out.printf("주식코드: %s | 이름: %s | 주가: %s | 종목: %s | 변동률: %s%n", stock.getCode(), stock.getName(), stock.getPrice(), stock.getCategory(), stock.getChangeRate());
        }
    }
}

