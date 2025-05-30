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

        sb.append("당신은 청소년의 투자 및 경제 성향을 분석하는 전문가입니다.\n");
        sb.append("다음은 한 학생의 최근 활동 로그입니다.\n\n");

        sb.append("이 활동 내용을 바탕으로 다음 다섯 가지 성격 특성에 대해 각각 0~100점 사이의 점수를 부여해주세요:\n");
        sb.append("- 공격성 (aggressiveness)\n");
        sb.append("- 적극성 (assertiveness)\n");
        sb.append("- 위험중립성 (risk neutrality)\n");
        sb.append("- 안정추구성 (security oriented)\n");
        sb.append("- 신중함 (calmness)\n\n");

        sb.append("그리고 다음 항목들을 포함하여 JSON 형태로 정확히 응답해 주세요:\n");
        sb.append("1. scores: 위의 다섯 특성에 대한 점수 (JSON 오브젝트)\n");
        sb.append("2. final_type: 이 학생의 최종 투자 성향을 요약한 키워드 \n");
        sb.append("예를 들어:\n");
        sb.append("- 공격투자형: 높은 수익을 선호하며 위험도 감수함\n");
        sb.append("- 적극투자형: 도전적이지만 일정한 안정도 고려함\n");
        sb.append("- 위험중립형: 수익과 위험을 균형 있게 바라봄\n");
        sb.append("- 안정추구형: 손실을 싫어하고 안정적인 자산을 선호함\n");
        sb.append("- 안정형: 매우 신중하고 보수적인 성향\n");
        sb.append("가장 높은 점수 하나만 보고 판단하지 말고,\n");
        sb.append("전체 특성의 조합과 패턴을 고려해서 종합적인 투자 성향(`final_type`)을 도출해 주세요.\n");
        sb.append("3. feedback: 학생 본인에게 전달할 간단한 피드백 (1문장)\n");
        sb.append("4. guidance: 부모가 이 학생의 성향을 이해하고 교육하는 데 도움이 될 수 있도록, 구체적인 지도 방법을 설명하는 2~3문장의 문단 형태 텍스트\n\n");

        sb.append("JSON 응답 예시는 다음과 같습니다 (꼭 이 구조로 반환해 주세요):\n");
        sb.append("{\n");
        sb.append("  \"scores\": {\n");
        sb.append("    \"공격성\": 85.0,\n");
        sb.append("    \"적극성\": 72.0,\n");
        sb.append("    \"위험중립성\": 63.0,\n");
        sb.append("    \"안정추구성\": 40.0,\n");
        sb.append("    \"신중함\": 25.0\n");
        sb.append("  },\n");
        sb.append("  \"final_type\": \"공격투자형\",\n");
        sb.append("  \"feedback\": \"학생은 도전적이며 높은 수익을 추구하는 성향입니다.\",\n");
        sb.append("  \"guidance\": \"자녀가 가진 높은 도전 성향을 긍정적으로 이끌어주세요. 실제 투자 사례를 함께 분석해보거나, 모의 주식 활동을 통해 스스로 판단하는 힘을 기를 수 있도록 도와주는 것이 좋습니다.\"\n");
        sb.append("}\n\n");

        sb.append("🧾 활동 로그:\n");
        for (ActivityLogDTO log : logs) {
            sb.append("- ").append(summarizeLog(log)).append("\n");
        }

        return sb.toString();
    }

    private String summarizeLog(ActivityLogDTO log) {
        String type = log.getType();
        Map<String, Object> data = log.getData();

        switch (type) {
            case "quiz":
                return String.format("퀴즈 %s에서 정답 여부는 %s였습니다.",
                        data.get("quiz_id"),
                        Boolean.TRUE.equals(data.get("correct")) ? "정답" : "오답");
            case "stock_simulation":
                return String.format("주식 '%s'에 대해 %s 액션으로 %s주를 거래했습니다.",
                        data.get("stock"),
                        data.get("action"),
                        data.get("amount"));
            default:
                return String.format("기타 활동: %s", data.toString());
        }
    }
}
