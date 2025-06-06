package com.moneykidsback.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizResultResponseDto {

    private int quizId;
    private boolean correct;
    private int points;
    private String solveDate;
}
