package com.moneykidsback.repository;

import com.moneykidsback.model.entity.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class StockRepositoryTest {

    @Autowired
    StockRepository stockRepository;

    @Test
    @DisplayName("종목 코드로 주식 찾기")
    public void findByCodeTest() {
        // 1. testdb에 더미 데이터 삽입
        Stock stock = new Stock();
        stock.setCode("005930");
        stock.setName("삼성전자");
        stock.setPrice(70000);
        stock.setCategory("IT");
        stockRepository.save(stock);

        // 2. 종목 코드로 주식 찾기
        List<Stock> foundStocks = stockRepository.findByCode("005930");

        // 3. 결과 검증
        assert foundStocks.size() == 1;
        Stock foundStock = foundStocks.get(0);
        assert "삼성전자".equals(foundStock.getName());
        assert 70000 == foundStock.getPrice();
        assert "IT".equals(foundStock.getCategory());
        assert "005930".equals(foundStock.getCode());
        System.out.println(foundStock);

        // 4. 더미 데이터 삭제 (테스트 후 정리)
        stockRepository.delete(stock);
        System.out.println("더미 데이터 삭제 완료");
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