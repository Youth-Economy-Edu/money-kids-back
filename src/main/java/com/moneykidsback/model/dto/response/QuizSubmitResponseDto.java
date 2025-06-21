package com.moneykidsback.model.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * 퀴즈 제출 처리 결과 응답 DTO
 */
@Getter
@Builder
public class QuizSubmitResponseDto {
    private int correctCount;
    private int totalCount;
    private int awardedPoints;
    private String message;
}
