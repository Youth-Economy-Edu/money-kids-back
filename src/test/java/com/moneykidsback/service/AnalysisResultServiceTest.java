package com.moneykidsback.service;


import com.moneykidsback.model.dto.response.AnalysisResultResponseDTO;
import com.moneykidsback.model.entity.TendencyAnalysis;
import com.moneykidsback.repository.TendencyAnalysisRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

// AnalysisResultService의 단위 테스트 작성
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AnalysisResultServiceTest {

    @InjectMocks
    private AnalysisResultService analysisResultService;

    @Mock
    private TendencyAnalysisRepository tendencyAnalysisRepository;

    // 테스트를 위한 사용자 ID
    @Test
    void getLatestResult_withValidUser_returnsResult() {
        // given
        String userId = "user123";
        TendencyAnalysis result = TendencyAnalysis.builder()
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .type("안정형")
                .aggressiveness(10.0)
                .assertiveness(5.0)
                .riskNeutrality(3.0)
                .securityOriented(2.0)
                .calmness(1.0)
                .feedback("안정적인 투자 성향입니다.")
                .build();

        given(tendencyAnalysisRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .willReturn(Optional.of(result));

        // when
        AnalysisResultResponseDTO response = analysisResultService.getLatestResult(userId);

        // then
        assertEquals("user123", response.getUserId());
        assertEquals("안정형", response.getAnalysisResult().getFinalType());
        assertTrue(response.getAnalysisResult().getScores().containsKey("안정형"));
        assertEquals(1.0, response.getAnalysisResult().getScores().get("안정형"));
    }
}
