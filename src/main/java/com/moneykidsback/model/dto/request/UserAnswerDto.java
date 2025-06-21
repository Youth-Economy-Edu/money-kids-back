package com.moneykidsback.model.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 사용자가 제출한 개별 답안을 담는 DTO
 */
@Getter
@Setter
public class UserAnswerDto {
    private int quizId;
    private String userAnswer; // "O" 또는 "X"
}
