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
        // 1. 프롬프트 생성
        String prompt = buildPromptFromLogs(activityLogs);

        // 2. LLM 호출
        String llmResponse = llmClient.requestAnalysis(prompt);
        System.out.println("📥 OpenAI raw response:\n" + llmResponse);

        // 3. JSON 응답 파싱
        try {
            // ✅ GPT가 JSON 객체 자체를 응답하므로 바로 파싱
            JsonNode result = objectMapper.readTree(llmResponse);

            JsonNode scores = result.get("scores");
            return tendencyAnalysisRepository.save(
                    TendencyAnalysis.builder()
                            .userId(userId)
                            .aggressiveScore(scores.get("공격투자형").asDouble())
                            .activeScore(scores.get("적극투자형").asDouble())
                            .neutralScore(scores.get("위험중립형").asDouble())
                            .stableSeekingScore(scores.get("안정추구형").asDouble())
                            .stableScore(scores.get("안정형").asDouble())
                            .type(result.get("final_type").asText())
                            .score(result.get("final_score").asDouble())
                            .feedback(result.get("feedback").asText())
                            .createdAt(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("LLM 응답 파싱 실패", e);
        }
    }

    private String buildPromptFromLogs(List<ActivityLogDTO> logs) {
        StringBuilder sb = new StringBuilder();

        sb.append("당신은 청소년 경제교육 분석 전문가입니다.\n");
        sb.append("다음은 한 학생의 최근 경제 활동 로그입니다.\n");
        sb.append("이 활동을 바탕으로 아래 다섯 가지 성향에 대해 각각 0~100점 사이 점수를 부여하고,\n");
        sb.append("가장 점수가 높은 성향을 final_type으로, 해당 점수를 final_score로 선택해 주세요.\n");
        sb.append("그리고 해당 성향에 대한 한 문장 피드백도 포함해주세요.\n\n");

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
        sb.append("  \"final_score\": 85.0,\n");
        sb.append("  \"feedback\": \"당신은 시장 평균 이상의 수익을 추구하며, 위험 감수에 적극적인 투자자입니다.\"\n");
        sb.append("}\n\n");

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
