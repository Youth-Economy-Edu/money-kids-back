package com.moneykidsback.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneykidsback.model.dto.request.ActivityLogDTO;
import com.moneykidsback.model.entity.TendencyAnalysis;
import com.moneykidsback.repository.TendencyAnalysisRepository;
import com.moneykidsback.service.client.LLMClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// 사용자의 활동 로그를 기반으로 투자 성향을 분석하고,
// LLM(대형 언어 모델)을 호출하여 분석 결과를 JSON 형식으로 반환
@Service
@RequiredArgsConstructor
public class AnalysisPerformService {

    private final LLMClient llmClient;
    private final TendencyAnalysisRepository tendencyAnalysisRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TendencyAnalysis performAnalysis(String userId, List<ActivityLogDTO> activityLogs) {
        String prompt = buildPromptFromLogs(activityLogs);
        String llmResponse = llmClient.requestAnalysis(prompt);
        System.out.println("📥 OpenAI raw response:\n" + llmResponse);

        try {
            JsonNode result = objectMapper.readTree(llmResponse);
            JsonNode scores = result.get("scores");
            JsonNode feedback = result.get("feedback");
            JsonNode guidance = result.get("guidance");

            // 한국어 → 내부 필드명 매핑
            double aggressiveness = scores.get("공격성").asDouble();
            double assertiveness = scores.get("적극성").asDouble();
            double riskNeutrality = scores.get("위험중립성").asDouble();
            double securityOriented = scores.get("안정추구성").asDouble();
            double calmness = scores.get("신중함").asDouble();

            return tendencyAnalysisRepository.save(
                    TendencyAnalysis.builder()
                            .userId(userId)
                            .aggressiveness(aggressiveness) // 공격성
                            .assertiveness(assertiveness) // 적극성
                            .riskNeutrality(riskNeutrality) // 위험중립성
                            .securityOriented(securityOriented) // 안정추구성
                            .calmness(calmness) // 신중함
                            .type(result.get("final_type").asText()) // 최종 투자 성향
                            .feedback(result.get("feedback").asText()) // 피드백
                            .guidance(guidance != null ? guidance.asText() : "") // 지도방법
                            .createdAt(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("LLM 응답 파싱 실패", e);
        }
    }

    // 활동 로그를 기반으로 LLM에 전달할 프롬프트를 생성
    private String buildPromptFromLogs(List<ActivityLogDTO> logs) {
        StringBuilder sb = new StringBuilder();

        sb.append("""
당신은 청소년의 경제 행동 데이터를 분석하여 투자 성향을 판단하는 전문가입니다.
아래는 한 학생의 활동 로그입니다. 이 활동들은 '퀴즈 풀이', '주식 모의 투자', '경제 콘텐츠 학습'의 세 가지 범주로 구성되어 있습니다.

다음의 성격 특성 5가지를 기준으로 이 학생의 투자 성향을 분석해 주세요:
1. 공격성 (aggressiveness): 높은 수익을 위해 위험을 감수하는 경향
2. 적극성 (assertiveness): 자신의 판단으로 능동적으로 행동하는 성향
3. 위험중립성 (risk neutrality): 수익과 손실을 균형 있게 고려하는 태도
4. 안정추구성 (security oriented): 안정적인 선택을 선호하고 손실을 회피하는 성향
5. 신중함 (calmness): 즉흥적이지 않고 충분히 생각 후 행동하는 성향

분석 시 참고 사항:
- 활동의 반복성, 일관성, 변화 추이 등을 고려해 주세요.
- 특정 주제나 행동 유형을 지속적으로 선호하는 경향이 있다면 반영해 주세요.
- 정답 여부나 투자 방식에서 드러나는 리스크 선택 성향을 잘 파악해 주세요.

최종 결과는 아래 JSON 형식으로 반환해 주세요:
{
  "scores": {
    "공격성": (0~100),
    "적극성": (0~100),
    "위험중립성": (0~100),
    "안정추구성": (0~100),
    "신중함": (0~100)
  },
  "final_type": "공격투자형 | 적극투자형 | 위험중립형 | 안정추구형 | 안정형",
  "feedback": "학생에게 전달할 짧은 요약 피드백 (1문장)",
  "guidance": "학부모가 학생을 이해하고 지도하는 데 도움이 되는 구체적 조언 (2~3문장)"
}

""");

        sb.append("📜 활동 로그 요약:\n");

        for (ActivityLogDTO log : logs) {
            sb.append("- ").append(summarizeLog(log)).append("\n");
        }

        return sb.toString();
    }

    // 활동 로그의 유형에 따라 요약 문자열을 생성
    private String summarizeLog(ActivityLogDTO log) {
        String type = log.getType();
        Map<String, Object> data = log.getData();

        switch (type) {
            case "quiz":
                return String.format(
                        "퀴즈 활동 - 카테고리: '%s', 난이도: '%s', 정답 여부: %s",
                        data.getOrDefault("quiz_category", "알 수 없음"),
                        data.getOrDefault("quiz_level", "알 수 없음"),
                        Boolean.TRUE.equals(data.get("correct")) ? "정답" : "오답"
                );

            case "stock_simulation":
                return String.format(
                        "주식 모의투자 - 산업군: '%s', 회사 규모: '%s', 액션: %s, 수량: %s",
                        data.getOrDefault("stock_category", "알 수 없음"),
                        data.getOrDefault("stock_company_size", "알 수 없음"),
                        data.getOrDefault("action", "알 수 없음"),
                        data.getOrDefault("amount", "알 수 없음")
                );

            case "content_completion":
                return String.format(
                        "학습 콘텐츠 완료 - 주제: '%s', 난이도: '%s'",
                        data.getOrDefault("worksheet_category", "알 수 없음"),
                        data.getOrDefault("worksheet_difficulty", "알 수 없음")
                );

            default:
                return String.format("기타 활동: %s", data.toString());
        }
    }
}
