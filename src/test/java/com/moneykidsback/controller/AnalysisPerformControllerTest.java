package com.moneykidsback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneykidsback.model.dto.request.ActivityLogDTO;
import com.moneykidsback.model.dto.request.AnalysisPerformRequestDTO;
import com.moneykidsback.model.entity.TendencyAnalysis;
import com.moneykidsback.service.AnalysisPerformService;
import com.moneykidsback.service.AnalysisResultService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// AnalysisPerformController의 performAnalysis 메서드를 테스트하는 클래스
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = AnalysisPerformController.class)
@WithMockUser
@Import(AnalysisPerformControllerTest.MockServiceConfig.class)
class AnalysisPerformControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AnalysisPerformService analysisPerformService;

    @Autowired
    private AnalysisResultService analysisResultService;

    // 분석 요청 테스트
    @Test
    @DisplayName("정상 분석 요청 → 200 OK")
    void performAnalysis_success() throws Exception {
        // given
        AnalysisPerformRequestDTO request = new AnalysisPerformRequestDTO();
        request.setUserId("user123");
        request.setActivityLogs(List.of(
                new ActivityLogDTO("quiz", "2025-05-10T12:00:00Z", Map.of(
                        "quiz_id", "q123", "answer", "O", "correct", true
                ))
        ));

        TendencyAnalysis fakeResult = TendencyAnalysis.builder()
                .userId("user123")
                .type("안정형")
                .feedback("당신은 안정적인 투자자입니다.")
                .createdAt(LocalDateTime.now())
                .aggressiveness(10.0)
                .assertiveness(5.0)
                .riskNeutrality(3.0)
                .securityOriented(2.0)
                .calmness(1.0)
                .build();

        // Mockito mock 설정
        when(analysisPerformService.performAnalysis(any(), any()))
                .thenReturn(fakeResult);

        // when + then
        mockMvc.perform(post("/api/analysis/perform")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("안정형"));
    }

    // 분석 기록 조회 테스트
    @Test
    @DisplayName("분석 기록 조회 → 200 OK")
    void getAnalysisHistory_success() throws Exception {
        // given
        TendencyAnalysis mockAnalysis = TendencyAnalysis.builder()
                .userId("user123")
                .type("공격투자형")
                .feedback("당신은 공격적인 투자자입니다.")
                .aggressiveness(90.0)
                .assertiveness(70.0)
                .riskNeutrality(60.0)
                .securityOriented(50.0)
                .calmness(40.0)
                .createdAt(LocalDateTime.now())
                .build();

        when(analysisResultService.getAnalysisHistory("user123"))
                .thenReturn(List.of(mockAnalysis));

        // when + then
        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/analysis/history")
                                .param("user_id", "user123")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].userId").value("user123"))
                .andExpect(jsonPath("$.data[0].analysisResult.finalType").value("공격투자형"));
    }

    // 분석 결과 삭제 테스트
    @Test
    @DisplayName("전체 분석 결과 삭제 성공 → 200 OK")
    void deleteAnalysis_success() throws Exception {
        // given
        Mockito.doNothing().when(analysisResultService).deleteAnalysisByUserId("user123");

        // when + then
        mockMvc.perform(delete("/api/analysis/result")
                        .param("user_id", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("200 ok"));    }

    // 분석 결과 삭제 테스트 - 분석 결과 없음
    @Test
    @DisplayName("분석 결과 없음 → 400 Bad Request")
    void deleteAnalysis_notFound() throws Exception {
        // given
        Mockito.doThrow(new IllegalArgumentException("분석 결과 없음"))
                .when(analysisResultService).deleteAnalysisByUserId("user123");

        // when + then
        mockMvc.perform(
                        delete("/api/analysis/result")
                                .param("user_id", "user123")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.msg").value("분석 결과 없음"));
    }

    // 새로운 mock 등록용 Config
    @TestConfiguration
    static class MockServiceConfig {
        @Bean
        public AnalysisPerformService analysisPerformService() {
            return Mockito.mock(AnalysisPerformService.class);
        }

        @Bean
        public AnalysisResultService analysisResultService() {
            return Mockito.mock(AnalysisResultService.class);
        }
    }
}
