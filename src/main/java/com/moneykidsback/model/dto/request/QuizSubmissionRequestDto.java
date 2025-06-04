package com.moneykidsback.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class QuizSubmissionRequestDto {
        private String userId;
        private int quizId;
        private String answer;
        private boolean correct;
        private int points;
        public boolean getCorrect() {
                return this.correct;
        }

}