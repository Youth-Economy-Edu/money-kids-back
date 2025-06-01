package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.ChatCompletionRequestDto;
import com.moneykidsback.model.dto.response.ChatCompletionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final WebClient openAiWebClient;

    public String getChatCompletionSync(String prompt) {
        // 요청 객체 만들어서
        ChatCompletionRequestDto request = new ChatCompletionRequestDto();
        request.setModel("gpt-4-turbo");
        request.setMessages(List.of(new ChatCompletionRequestDto.Message("user", prompt)));

        // OpenAI API에 POST 요청
        ChatCompletionResponse response = openAiWebClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .block(); // block()이 붙으면 '동기적으로' 응답을 기다려!

        // 응답에서 텍스트만 추출해서 리턴
        return response.getChoices().get(0).getMessage().getContent();
    }

}
