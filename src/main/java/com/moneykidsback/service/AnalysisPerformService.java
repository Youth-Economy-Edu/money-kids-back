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

            // 3. 분석 결과 저장
            return tendencyAnalysisRepository.save(
                    TendencyAnalysis.builder()
                            .userId(userId)
                            .type(result.get("type").asText())
                            .score(result.get("score").asDouble())
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

        sb.append("당신은 청소년 경제 교육 분석 전문가입니다.\n");
        sb.append("아래는 한 학생의 최근 경제 활동 로그입니다.\n");
        sb.append("이 데이터를 바탕으로 학생의 투자 성향을 분석하고,\n");
        sb.append("다음 JSON 형식으로만 응답해주세요. 설명없이 JSON만 반환하세요. 다른 문장은 출력하지 마세요. \n\n");

        sb.append("형식 예시:\n");
        sb.append("{\n");
        sb.append("  \"type\": \"모험형\",\n");
        sb.append("  \"score\": 91.2,\n");
        sb.append("  \"feedback\": \"우리 자녀는 도전적인 투자 성향을 보이고 있어요.\"\n");
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
