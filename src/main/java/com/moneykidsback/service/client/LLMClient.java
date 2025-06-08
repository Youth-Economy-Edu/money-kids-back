package com.moneykidsback.service.client;

import org.springframework.stereotype.Component;

// LLM 클라이언트의 계약을 정의
@Component
public interface LLMClient {
    String requestAnalysis(String prompt);

}
