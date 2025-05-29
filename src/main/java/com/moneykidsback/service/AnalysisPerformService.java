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

            // 한국어 → 내부 필드명 매핑
            double aggressiveness = scores.get("공격투자형").asDouble();
            double assertiveness = scores.get("적극투자형").asDouble();
            double riskNeutrality = scores.get("위험중립형").asDouble();
            double securityOriented = scores.get("안정추구형").asDouble();
            double calmness = scores.get("안정형").asDouble();

            return tendencyAnalysisRepository.save(
                    TendencyAnalysis.builder()
                            .userId(userId)
                            .aggressiveness(aggressiveness)
                            .assertiveness(assertiveness)
                            .riskNeutrality(riskNeutrality)
                            .securityOriented(securityOriented)
                            .calmness(calmness)
                            .type(result.get("final_type").asText())
                            .feedback(result.get("feedback").asText())
                            .createdAt(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("LLM 응답 파싱 실패", e);
        }
    }

    // 활동 로그를 기반으로 LLM에 전달할 프롬프트를 생성
    // 활동 로그를 기반으로 LLM에 전달할 프롬프트를 생성
    private String buildPromptFromLogs(List<ActivityLogDTO> logs) {
        StringBuilder sb = new StringBuilder();

        sb.append("당신은 청소년의 투자 및 경제 성향을 분석하는 전문가입니다.\n");
        sb.append("다음은 한 학생의 최근 활동 로그입니다.\n");
        sb.append("이 활동 내용을 바탕으로 아래 다섯 가지 성격 특성에 대해 각각 0~100점 사이의 점수를 부여해주세요:\n");
        sb.append("- 공격성 (aggressiveness)\n");
        sb.append("- 자기주장성 (assertiveness)\n");
        sb.append("- 위험중립성 (risk neutrality)\n");
        sb.append("- 안정추구성 (security oriented)\n");
        sb.append("- 차분함 (calmness)\n\n");

        sb.append("그리고 전체 점수를 종합적으로 판단하여 해당 학생의 최종 투자 성향을 `final_type`으로 제시하고,\n");
        sb.append("그에 맞는 1문장 피드백(`feedback`)을 제공해주세요.\n");
        sb.append("가장 높은 점수만으로 판단하지 말고, 종합적인 패턴과 성격 조합을 고려해주세요.\n\n");

        sb.append("JSON 응답 형식은 다음과 같습니다. 설명 없이 반드시 이 구조만 그대로 반환해주세요:\n\n");
        sb.append("{\n");
        sb.append("  \"scores\": {\n");
        sb.append("    \"공격투자형\": 85.0,\n");
        sb.append("    \"적극투자형\": 72.0,\n");
        sb.append("    \"위험중립형\": 63.0,\n");
        sb.append("    \"안정추구형\": 40.0,\n");
        sb.append("    \"안정형\": 25.0\n");
        sb.append("  },\n");
        sb.append("  \"final_type\": \"공격투자형\",\n");
        sb.append("  \"feedback\": \"...\"\n");
        sb.append("}\n");

        sb.append("학생 활동 로그:\n");
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
