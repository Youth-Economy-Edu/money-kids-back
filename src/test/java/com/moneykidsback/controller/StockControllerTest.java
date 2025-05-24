// java
package com.moneykidsback.controller;

import com.moneykidsback.model.Stock;
import com.moneykidsback.service.RankingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class) //테스트 할 컨트롤러 로드
@Import(StockControllerTest.MockConfig.class) //컨트롤러 목업 생성 후 Bean 등록
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock //테스트용오르 Service에 대한 목업 생성
    private RankingService rankingService;

    static class MockConfig {
        @Bean
        RankingService rankingService() {
            return org.mockito.Mockito.mock(RankingService.class);
        }
    }

    @Test
    @DisplayName("전체 주식 순위 조회 - 성공")
    void getStockRanking_Success() throws Exception {

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

        //GET 요청에 대한 응답 결과 검증
        mockMvc.perform(get("/api/stocks/rankings")) //GET 요청
                .andExpect(status().isOk()) // 정상응답(200) 확인
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //응답 데이터 타입 확인
                .andExpect(jsonPath("$[0].code").value("CODE1")) //응답 JSON의 첫번쨰 데이터 확인
                .andExpect(jsonPath("$[1].code").value("CODE2")); //응답 JSON의 두번째 데이터 확인
    }


    @Test
    @DisplayName("카테고리별 주식 순위 조회 - 데이터 없음")
    void getStockRankingByCategory_NoContent() throws Exception {
        // 해당 카테고리에 대한 데이터 없을때 예외처리 (빈 리스트 반환)
        when(rankingService.findAllByCategoryOrderByPriceDesc("NonExistentCategory")).thenReturn(Collections.emptyList());

        // GET요청
        // 존재하지 않는 카테고리에 대한 204응답 검증
        mockMvc.perform(get("/api/stocks/rankings")
                        .param("standard", "NonExistentCategory"))
                .andExpect(status().isNoContent());
    }
}