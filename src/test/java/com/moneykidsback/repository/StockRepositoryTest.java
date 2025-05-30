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
            stock.setUpdatedAt(midnight);      // 자정 시간

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
            System.out.printf("code: "+ stock.getCode() + "| name: "+stock.getName()+" | price: "+stock.getPrice()+" | category: "+stock.getCategory()+" | updateTime: "+stock.getUpdatedAt()+"\n");
        }    }


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
            System.out.printf("code: "+ dto.getCode() + "| name: "+dto.getName()+" | price: "+dto.getPrice()+" | category: "+dto.getCategory()+" | updateTime: "+dto.getUpdateAt()+" | changeRate: %.2f%%\n", dto.getChangeRate());
        }
        //변동률 순위 검증
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1).getChangeRate() >= result.get(i).getChangeRate());
        }
    }
}