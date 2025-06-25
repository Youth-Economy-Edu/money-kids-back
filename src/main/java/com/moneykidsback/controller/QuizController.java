package com.moneykidsback.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.model.dto.request.QuizSubmissionRequestDto;
import com.moneykidsback.model.dto.response.QuizResultResponseDto;
import com.moneykidsback.model.entity.Quiz;
import com.moneykidsback.service.ActivityLogService;
import com.moneykidsback.service.DailyQuestService;
import com.moneykidsback.service.QuizService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 🧩 경제 OX 퀴즈 컨트롤러
 * - 난이도별 퀴즈 (기초/중급/고급)
 * - 카테고리별 퀴즈 (저축/투자/소비 등)
 * - 실시간 채점 및 해설 제공
 * - 포인트 리워드 시스템
 */
@Tag(name = "Quiz", description = "경제 퀴즈 관리")
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private ActivityLogService activityLogService;
    
    @Autowired
    private DailyQuestService dailyQuestService;

    @Operation(summary = "퀴즈 제출", description = "퀴즈 답안을 제출하고 포인트를 지급받습니다")
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmissionRequestDto dto) {
        try {
            // 퀴즈 제출 처리 (포인트 지급 포함)
            quizService.submitQuiz(dto);
            
            // 활동 로그 기록
            activityLogService.logQuiz(
                dto.getUserId(), 
                "기본", // 퀴즈 레벨 (필요시 dto에서 받아올 수 있음)
                "경제기초", // 퀴즈 카테고리 (필요시 dto에서 받아올 수 있음)
                dto.getCorrect() ? "CORRECT" : "WRONG"
            );
            
            // 일일 퀘스트 진행도 업데이트
            dailyQuestService.updateProgress(dto.getUserId(), "QUIZ", 1);
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", Map.of(
                    "correct", dto.getCorrect(),
                    "points", dto.getCorrect() ? dto.getPoints() : 0,
                    "message", dto.getCorrect() ? "정답입니다! 포인트를 획득했습니다." : "오답입니다. 다시 도전해보세요!"
                ),
                "msg", "퀴즈 제출이 완료되었습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "퀴즈 제출 실패: " + e.getMessage()
            ));
        }
    }
    
    @Operation(summary = "퀴즈 결과 조회", description = "사용자의 퀴즈 결과를 조회합니다")
    @GetMapping("/result")
    public ResponseEntity<?> getQuizResults(
            @Parameter(description = "사용자 ID", required = true) @RequestParam("user_id") String userId) {
        try {
            List<QuizResultResponseDto> results = quizService.getUserQuizResults(userId);
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", results,
                "msg", "퀴즈 결과 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "퀴즈 결과 조회 실패: " + e.getMessage()
            ));
        }
    }
    
    @Operation(summary = "랜덤 퀴즈 조회", description = "난이도별 랜덤 퀴즈를 조회합니다")
    @GetMapping("/random")
    public ResponseEntity<?> getRandomQuizzes(
            @Parameter(description = "퀴즈 난이도", required = true) @RequestParam("level") String level) {
        try {
            List<Quiz> quizzes = quizService.getRandomQuizzesByLevel(level);
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", quizzes,
                "msg", "랜덤 퀴즈 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "퀴즈 조회 실패: " + e.getMessage()
            ));
        }
    }
    
    @Operation(summary = "퀴즈 세션 완료", description = "퀴즈 세션을 완료하고 포인트를 지급받습니다")
    @PostMapping("/session/complete")
    public ResponseEntity<?> completeQuizSession(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            Integer quizLevel = (Integer) request.get("quizLevel");
            Integer totalQuestions = (Integer) request.get("totalQuestions");
            Integer correctAnswers = (Integer) request.get("correctAnswers");
            
            Map<String, Object> result = quizService.completeQuizSession(userId, quizLevel, totalQuestions, correctAnswers);
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", result,
                "msg", "퀴즈 세션 완료"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "퀴즈 세션 완료 실패: " + e.getMessage()
            ));
        }
    }
    
    @Operation(summary = "퀴즈 진행 현황 조회", description = "사용자의 퀴즈 진행 현황을 조회합니다")
    @GetMapping("/user/{userId}/progress")
    public ResponseEntity<?> getUserQuizProgress(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String userId) {
        try {
            Map<String, Object> progress = quizService.getUserQuizProgress(userId);
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "data", progress,
                "msg", "퀴즈 진행 현황 조회 성공"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "code", 500,
                "data", null,
                "msg", "퀴즈 진행 현황 조회 실패: " + e.getMessage()
            ));
        }
    }
}
