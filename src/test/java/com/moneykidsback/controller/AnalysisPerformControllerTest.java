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

    @Test
    @DisplayName("정상 분석 요청 → 200 OK")
    void performAnalysis_success() throws Exception {
        // given
        AnalysisPerformRequestDTO request = new AnalysisPerformRequestDTO();
        request.setUserId(String.valueOf(1L));
        request.setActivityLogs(List.of(
                new ActivityLogDTO("quiz", "2025-05-10T12:00:00Z", Map.of(
                        "quiz_id", "q123", "answer", "O", "correct", true
                ))
        ));

        TendencyAnalysis fakeResult = TendencyAnalysis.builder()
                .userId(String.valueOf(1L))
                .type("안정형")
                .score(87.5)
                .feedback("당신은 안정적인 투자자입니다.")
                .createdAt(LocalDateTime.now())
                .build();

        // Mockito mock 설정
        when(analysisPerformService.performAnalysis(any(), any()))
                .thenReturn(fakeResult);

        // when + then
        mockMvc.perform(post("/api/analysis/perform")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("안정형"))
                .andExpect(jsonPath("$.data.score").value(87.5));
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
