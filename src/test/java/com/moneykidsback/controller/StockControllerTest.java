// java
package com.moneykidsback.controller;

import com.moneykidsback.model.dto.response.StockChangeRateDto;
import com.moneykidsback.model.entity.Stock;
import com.moneykidsback.repository.StockRepository;
import com.moneykidsback.service.RankingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.mockito.Mockito.when;

@WebMvcTest(StockController.class) //테스트 할 컨트롤러 로드
@Import(StockControllerTest.MockConfig.class) //컨트롤러 목업 생성 후 Bean 등록
@AutoConfigureMockMvc(addFilters = false)   // ← 필터 무시!
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StockRepository stockRepository;

    @Mock //테스트용으로 Service에 대한 목업 생성
    private RankingService rankingService;

    static class MockConfig {
        @Bean
        RankingService rankingService() {
            return org.mockito.Mockito.mock(RankingService.class);
        }

        @Bean
        StockRepository stockRepository() {
            return org.mockito.Mockito.mock(StockRepository.class);
        }
    }

    @Test
    @DisplayName("전체 주가 순위 조회")
    void getStockRanking() throws Exception {

        //더미데이터 생성
        Stock stock1 = new Stock();
        stock1.setCode("CODE1");
        stock1.setName("Stock1");
        stock1.setPrice(1000);
        stock1.setCategory("Tech");

        Stock stock2 = new Stock();
        stock2.setCode("CODE2");
        stock2.setName("Stock2");
        stock2.setPrice(900);
        stock2.setCategory("Finance");

        // 컨트롤러의 동작만을 검증하기 위한것이기 떄문에 실제 메소드 대신 더미데이터가 출력되게 함
        when(rankingService.getStocksOrderedByPriceDesc()).thenReturn(Arrays.asList(stock1, stock2));

        List<Stock> result = rankingService.getStocksOrderedByPriceDesc();

        for (Stock d : result) {
            System.out.printf("코드: %s | 이름: %s | 가격: %d | 카테고리: %s\n",
                    d.getCode(), d.getName(), d.getPrice(), d.getCategory());
        }
        //GET 요청에 대한 응답 결과 검증
//        mockMvc.perform(get("/api/stocks/ranking?standard=price")) //GET 요청
//                .andExpect(status().isOk()) // 정상응답(200) 확인
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //응답 데이터 타입 확인
//                .andExpect(jsonPath("$[0].code").value("CODE1")) //응답 JSON의 첫번쨰 데이터 확인
//                .andExpect(jsonPath("$[1].code").value("CODE2")); //응답 JSON의 두번째 데이터 확인
    }


    @Test
    @DisplayName("변동률 기준 순위 조회 - 성공")
    void getStockRankingTest() throws Exception {
        // 더미 데이터 생성
        Stock stockOld = new Stock();
        stockOld.setCode("CODE1");
        stockOld.setName("테스트 스톡1");
        stockOld.setPrice(1000);
        stockOld.setBeforePrice(1000);
        stockOld.setCategory("Tech");
        stockOld.setUpdateAt(LocalDateTime.now().minusDays(1));

        Stock stockNew = new Stock();
        stockNew.setCode("CODE1");
        stockNew.setName("테스트 스톡1");
        stockNew.setPrice(1200);
        stockNew.setBeforePrice(1000);
        stockNew.setCategory("Tech");
        stockNew.setUpdateAt(LocalDateTime.now());

        Stock stockOld2 = new Stock();
        stockOld2.setCode("CODE2");
        stockOld2.setName("테스트 스톡2");
        stockOld2.setPrice(1000);
        stockOld2.setBeforePrice(1000);
        stockOld2.setCategory("Tech");
        stockOld2.setUpdateAt(LocalDateTime.now().minusDays(1));

        Stock stockNew2 = new Stock();
        stockNew2.setCode("CODE2");
        stockNew2.setName("테스트 스톡2");
        stockNew2.setPrice(9000);
        stockNew2.setBeforePrice(1000);
        stockNew2.setCategory("Tech");
        stockNew2.setUpdateAt(LocalDateTime.now());

        // 변동률 계산
        double changeRate1 = ((double) (stockNew.getPrice() - stockOld.getBeforePrice()) / stockOld.getBeforePrice()) * 100;
        double changeRate2 = ((double) (stockNew2.getPrice() - stockOld2.getBeforePrice()) / stockOld2.getBeforePrice()) * 100;

        // Mock 반환값 지정
        when(rankingService.getStocksOrderedByChangeRateDesc()).thenReturn(
                Arrays.asList(
                        new StockChangeRateDto(stockNew2.getCode(), stockNew2.getName(), stockNew2.getPrice(),
                                stockNew2.getCategory(), stockNew2.getUpdateAt(), changeRate2),
                        new StockChangeRateDto(stockNew.getCode(), stockNew.getName(), stockNew.getPrice(),
                                stockNew.getCategory(), stockNew.getUpdateAt(), changeRate1)
                )
        );

        List<StockChangeRateDto> result = rankingService.getStocksOrderedByChangeRateDesc();

        for (StockChangeRateDto d : result) {
            System.out.printf("코드: %s | 이름: %s | 가격: %d | 카테고리: %s | 변동률: %s\n",
                    d.getCode(), d.getName(), d.getPrice(), d.getCategory(), d.getChangeRate());
        }

        // GET 요청 및 응답 검증
//        mockMvc.perform(get("/api/stocks/ranking")
//                        .param("standard", "changeRate"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$[0].code").value("CODE2"))
//                .andExpect(jsonPath("$[1].code").value("CODE1"));

    }
}