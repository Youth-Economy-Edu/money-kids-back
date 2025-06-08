package com.moneykidsback.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

// 요청을 위한 DTO 클래스
@Data
@Getter
@Setter
public class ChatCompletionRequestDto {
    private String model;
    private List<Message> messages;

    @Data
    public static class Message {
        private String role;
        private String content;

        public Message(String user, String prompt) {
            this.role = user;
            this.content = prompt;
        }
    }
}
