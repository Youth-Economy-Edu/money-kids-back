package com.moneykidsback.controller;

import com.moneykidsback.service.NewsGenerateService;
import com.moneykidsback.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class ChatController {

    private final OpenAiService openAiService;
    private final NewsGenerateService newsGenerateService;

    @PostMapping("/generate")
    public List<String> generateArticleByStock() throws InterruptedException {
        return newsGenerateService.generateAndSaveNewsForAllStocks();
    }
}
