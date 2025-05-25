package com.moneykidsback.service;


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

    @Test
    void getLatestResult_withValidUser_returnsResult() {
        // given
        Long userId = 1L;
        TendencyAnalysis result = new TendencyAnalysis(
                null, userId, "안정형", 85.0, "위험 회피 성향입니다.", LocalDateTime.now()
        );

        given(tendencyAnalysisRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .willReturn(Optional.of(result));

        // when
        Optional<TendencyAnalysis> response = analysisResultService.getLatestResult(userId);

        // then
        assertTrue(response.isPresent());
        assertEquals("안정형", response.get().getType());
    }

}
