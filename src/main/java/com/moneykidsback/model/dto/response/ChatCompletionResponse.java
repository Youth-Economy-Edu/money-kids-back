package com.moneykidsback.model.dto.response;

import lombok.Data;

import java.util.List;

//API 응답을 위한 DTO 클래스
@Data
public class ChatCompletionResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private int index;
        private Message message;

        @Data
        public static class Message {
            private String role;
            private String content;
        }
    }
}
