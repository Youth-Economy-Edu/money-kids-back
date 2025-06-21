package com.moneykidsback.controller;

import com.moneykidsback.model.dto.request.QuizSubmitRequestDto; // (새로 생성될 DTO)
import com.moneykidsback.model.dto.response.QuizResultResponseDto;
import com.moneykidsback.model.dto.response.QuizSubmitResponseDto; // (새로 생성될 DTO)
import com.moneykidsback.model.entity.Quiz;
import com.moneykidsback.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor // 의존성 주입 방식을 생성자 주입으로 변경합니다.
public class QuizController {

    private final QuizService quizService;
    // ActivityLogService, DailyQuestService 의존성은 QuizService로 이동했으므로 제거

    // [기존 유지] 난이도별 랜덤 퀴즈 조회
    @GetMapping("/random")
    public ResponseEntity<List<Quiz>> getRandomQuizzes(@RequestParam("level") String level) {
        // 응답 형식을 프론트엔드에서 바로 사용하기 쉽게 List<Quiz>로 변경
        List<Quiz> quizzes = quizService.getRandomQuizzesByLevel(level);
        return ResponseEntity.ok(quizzes);
    }

    // [API 엔드포인트 변경] 퀴즈 세트 제출 및 채점/보상 요청
    @PostMapping("/submit")
    public ResponseEntity<QuizSubmitResponseDto> submitQuizSet(
        @RequestBody QuizSubmitRequestDto submitDto,
        @AuthenticationPrincipal UserDetails userDetails) {

        // Spring Security를 통해 인증된 사용자 ID를 가져옵니다.
        String userId = userDetails.getUsername();

        QuizSubmitResponseDto response = quizService.submitQuizAndProcess(userId, submitDto);
        return ResponseEntity.ok(response);
    }

    // [기존 유지] 퀴즈 결과 조회
    @GetMapping("/result")
    public ResponseEntity<List<QuizResultResponseDto>> getQuizResults(@RequestParam("user_id") String userId) {
        List<QuizResultResponseDto> results = quizService.getUserQuizResults(userId);
        return ResponseEntity.ok(results);
    }
}
