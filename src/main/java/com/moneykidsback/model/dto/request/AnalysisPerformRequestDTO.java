package com.moneykidsback.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

// 사용자의 행동 로그를 포함하여 분석 요청을 나타내는 DTO
@Data
public class AnalysisPerformRequestDTO {
    @JsonProperty("user_id")
    private String userId; // 사용자 ID

    @JsonProperty("activity_logs")
    private List<ActivityLogDTO> activityLogs; // 사용자 행동 로그
}
