package com.moneykidsback.model.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * 퀴즈 세트 전체의 제출 정보를 담는 DTO
 */
@Getter
@Setter
public class QuizSubmitRequestDto {
    private int difficulty;
    private List<UserAnswerDto> answers;
}
