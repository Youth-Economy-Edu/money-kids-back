package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.ActivityLogDTO;
import com.moneykidsback.model.entity.TendencyAnalysis;
import com.moneykidsback.repository.TendencyAnalysisRepository;
import com.moneykidsback.service.client.LLMClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

// AnalysisPerformService의 performAnalysis 메서드를 테스트하는 클래스
@ExtendWith(MockitoExtension.class)
public class AnalysisPerformServiceTest {

    @InjectMocks
    private AnalysisPerformService analysisPerformService;

    @Mock
    private LLMClient llmClient;

    @Mock
    private TendencyAnalysisRepository tendencyAnalysisRepository;

    @Test
    void performAnalysis_withValidLogs_returnsResult() throws Exception {
        // given
        // 테스트를 위한 사용자 ID와 활동 로그
        Long userId = 1L;
        List<ActivityLogDTO> activityLogs = List.of(
                new ActivityLogDTO("quiz", "2025-05-10T12:00:00Z", Map.of(
                        "quiz_id", "q123",
                        "answer", "O",
                        "correct", true
                )),
                new ActivityLogDTO("stock_simulation", "2025-05-11T13:00:00Z", Map.of(
                        "action", "buy",
                        "stock", "삼성전자",
                        "amount", 5
                ))
        );

        String fakeLlmResponse = """
            {
              "type": "모험형",
              "score": 88.5,
              "feedback": "당신은 도전적인 투자자입니다."
            }
        """;

        given(llmClient.requestAnalysis(any())).willReturn(fakeLlmResponse);

        // when
        TendencyAnalysis result = analysisPerformService.performAnalysis(String.valueOf(userId), activityLogs);

        // then
        assertEquals("모험형", result.getType());
        assertEquals("당신은 도전적인 투자자입니다.", result.getFeedback());
        verify(tendencyAnalysisRepository).save(any(TendencyAnalysis.class));
    }
}
