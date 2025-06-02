package com.moneykidsback.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSubmissionRequestDto {

    private String userId;     // 사용자 ID
    private int quizId;        // 퀴즈 ID
    private String answer;     // 사용자가 제출한 답
    private boolean isCorrect; // 정답 여부
    private int points;        // 획득한 포인트
}
