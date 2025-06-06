package com.moneykidsback.controller;

import com.moneykidsback.model.dto.request.QuizSubmissionRequestDto;
import com.moneykidsback.model.dto.response.QuizResultResponseDto;
import com.moneykidsback.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/submit")
    public ResponseEntity<String> submitQuiz(@RequestBody QuizSubmissionRequestDto dto) {
        quizService.submitQuiz(dto);
        return ResponseEntity.ok("퀴즈 제출이 완료되었습니다.");
    }
    @GetMapping("/result")
    public ResponseEntity<List<QuizResultResponseDto>> getQuizResults(@RequestParam("user_id") String userId) {
        List<QuizResultResponseDto> result = quizService.getUserQuizResults(userId);
        return ResponseEntity.ok(result);
    }

}

