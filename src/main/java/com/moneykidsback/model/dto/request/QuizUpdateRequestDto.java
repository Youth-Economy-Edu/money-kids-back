package com.moneykidsback.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizUpdateRequestDto {
    private String question;
    private String answer;
    private String explanation;
    private String level;
}
