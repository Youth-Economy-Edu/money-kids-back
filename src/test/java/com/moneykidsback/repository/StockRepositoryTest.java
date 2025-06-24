package com.moneykidsback.repository;


import com.moneykidsback.model.dto.response.StockChangeRateDto;
import com.moneykidsback.model.entity.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        stockRepository.deleteAll();
    }


    @Test
    @DisplayName("10개 주식, 1회 가격변동 더미데이터 생성 (자정 기준 beforePrice 저장)")
    public void StocksPriceChangesTest() {
        List<Stock> stocks = new ArrayList<>();
        LocalDateTime midnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);

        for (int i = 1; i <= 10; i++) {
            Stock stock = new Stock();
            stock.setCode("CODE-" + i);
            stock.setName("테스트 스톡-" + i);
            stock.setCategory("Tech");

            int beforePrice = 1000 * i;
            int price = beforePrice + (int) (Math.random() * 500) - 250; // -250~+249 변동

            stock.setBeforePrice(beforePrice); // 자정 기준 가격
            stock.setPrice(price);             // 변동 후 가격
            stock.setUpdateAt(midnight);      // 자정 시간

            stocks.add(stock);
        }
        stockRepository.saveAll(stocks);
    }


    @Test
    @DisplayName("가격기준 순위 조회")
    public void findAllByOrderByPriceDescTest() {
        setUp();
        StocksPriceChangesTest();

        List<Stock> stocks = stockRepository.findAllByOrderByPriceDesc();

        // 검증: 가격이 내림차순으로 정렬되어 있는지 확인
        for (int i = 1; i < stocks.size(); i++) {
            Stock prev = stocks.get(i - 1);
            Stock curr = stocks.get(i);
            assertTrue(prev.getPrice() >= curr.getPrice());
        }
        for (Stock stock : stocks) {
            System.out.printf("code: %s | name: %s | price: %d | category: %s | updateTime: %s\n",
                stock.getCode(), stock.getName(), stock.getPrice(), stock.getCategory(), stock.getUpdateAt());
        }
    }

    @Test
    @DisplayName("이름으로 종목 찾기")
    public void findByNameTest() {
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

        // 2. 이름으로 종목 찾기
        List<Stock> foundStocks = stockRepository.findByName("삼성전자");

        // 3. 결과 검증
        assert foundStocks.size() == 1;
        Stock foundStock = foundStocks.get(0);
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

    @Test
    @DisplayName("주가 변동률 검색")
    public void stockChangeRateTest() {
        // todo: 주식 테이블에 beforPrice 컬럼 필요함
        StocksPriceChangesTest(); // 더미 데이터 저장

        List<StockChangeRateDto> result = stockRepository.findAllOrderByChangeRateDescWithDto();

        // 검증: 변동률이 내림차순으로 정렬되어 있는지 확인
        System.out.println("결과 개수: " + result.size());
        for (StockChangeRateDto dto : result) {
            System.out.println(dto.getCode() + " | " + dto.getChangeRate());
        }

        for (StockChangeRateDto dto : result) {
            System.out.printf("code: %s | name: %s | price: %d | category: %s | updateTime: %s | changeRate: %.2f%%\n",
                dto.getCode(), dto.getName(), dto.getPrice(), dto.getCategory(), dto.getUpdateAt(), dto.getChangeRate());
        }
        //변동률 순위 검증
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1).getChangeRate() >= result.get(i).getChangeRate());
        }
    }

    @Test
    @DisplayName("카테고리로 종목 찾기")
    public void findByCategoryTest() {
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

        // 2. 카테고리로 종목 찾기
        List<Stock> foundStocks = stockRepository.findByCategory("IT");

        // 3. 결과 검증
        assert foundStocks.size() == 2;
        for (Stock foundStock : foundStocks) {
            assert "IT".equals(foundStock.getCategory());
            System.out.println(foundStock);
        }

        // 4. 더미 데이터 삭제 (테스트 후 정리)
        stockRepository.delete(stock1);
        stockRepository.delete(stock2);
        System.out.println("더미 데이터 삭제 완료");
    }
}