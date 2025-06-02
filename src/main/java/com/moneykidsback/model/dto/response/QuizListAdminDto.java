package com.moneykidsback.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizListAdminDto {

    private int quizId;
    private String question;
    private String answer;
}
