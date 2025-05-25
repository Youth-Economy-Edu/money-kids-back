package com.moneykidsback.model.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

// 유저 행동 로그 나타내는 DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLogDTO {
    private String type; // 퀴즈, 투자 행동
    private String timeStamp; // 행동 발생 시각
    private Map<String, Object> data; // 행동에 대한 정보 (예: 투자 금액, 퀴즈 정답 등)
}
