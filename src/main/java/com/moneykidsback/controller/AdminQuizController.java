package com.moneykidsback.controller;

import com.moneykidsback.model.dto.request.QuizCreateRequestDto;
import com.moneykidsback.model.dto.request.QuizUpdateRequestDto;
import com.moneykidsback.model.dto.response.QuizDetailAdminDto;
import com.moneykidsback.model.dto.response.QuizListAdminDto;
import com.moneykidsback.model.entity.Quiz;
import com.moneykidsback.repository.QuizRepository;
import com.moneykidsback.service.QuizService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Admin Quiz", description = "관리자 퀴즈 관리")
@RestController
@RequestMapping("/api/admin/quizzes")
public class AdminQuizController {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuizService quizService;

    // ✅ 1. 퀴즈 등록 (중복 방지)
    @Operation(summary = "퀴즈 등록", description = "새로운 퀴즈를 등록합니다 (중복 방지)")
    @PostMapping
    public ResponseEntity<String> createQuiz(@RequestBody QuizCreateRequestDto dto) {
        boolean exists = quizRepository.existsByQuestionAndLevel(dto.getQuestion(), dto.getLevel());
        if (exists) {
            return ResponseEntity.badRequest().body("이미 동일한 질문이 해당 레벨에 존재합니다.");
        }

        Quiz quiz = new Quiz();
        quiz.setQuestion(dto.getQuestion());
        quiz.setAnswer(dto.getAnswer());
        quiz.setExplanation(dto.getExplanation());
        quiz.setLevel(dto.getLevel());

        quizRepository.save(quiz);
        return ResponseEntity.ok("퀴즈가 등록되었습니다.");
    }

    // ✅ 2. 퀴즈 수정
    @Operation(summary = "퀴즈 수정", description = "기존 퀴즈를 수정합니다")
    @PutMapping("/{quizId}")
    public ResponseEntity<String> updateQuiz(
            @Parameter(description = "퀴즈 ID", required = true) @PathVariable int quizId,
            @RequestBody QuizUpdateRequestDto dto) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("퀴즈를 찾을 수 없습니다."));

        quiz.setQuestion(dto.getQuestion());
        quiz.setAnswer(dto.getAnswer());
        quiz.setExplanation(dto.getExplanation());
        quiz.setLevel(dto.getLevel());

        quizRepository.save(quiz);
        return ResponseEntity.ok("퀴즈가 수정되었습니다.");
    }

    // ✅ 3. 퀴즈 삭제
    @Operation(summary = "퀴즈 삭제", description = "퀴즈를 삭제합니다")
    @DeleteMapping("/{quizId}")
    public ResponseEntity<String> deleteQuiz(
            @Parameter(description = "퀴즈 ID", required = true) @PathVariable int quizId) {
        if (!quizRepository.existsById(quizId)) {
            return ResponseEntity.notFound().build();
        }

        quizRepository.deleteById(quizId);
        return ResponseEntity.ok("퀴즈가 삭제되었습니다.");
    }

    // ✅ 4. 퀴즈 목록 조회 (레벨별)
    @Operation(summary = "퀴즈 목록 조회", description = "레벨별 퀴즈 목록을 조회합니다")
    @GetMapping("/list")
    public ResponseEntity<List<QuizListAdminDto>> getQuizList(
            @Parameter(description = "퀴즈 레벨", required = true) @RequestParam("level") String level) {
        List<Quiz> quizList = quizRepository.findByLevel(level);

        List<QuizListAdminDto> result = quizList.stream().map(quiz -> {
            QuizListAdminDto dto = new QuizListAdminDto();
            dto.setQuizId(quiz.getId());
            dto.setQuestion(quiz.getQuestion());
            dto.setAnswer(quiz.getAnswer());
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ✅ 5. 퀴즈 상세 조회
    @Operation(summary = "퀴즈 상세 조회", description = "특정 퀴즈의 상세 정보를 조회합니다")
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDetailAdminDto> getQuizDetail(
            @Parameter(description = "퀴즈 ID", required = true) @PathVariable int quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("퀴즈를 찾을 수 없습니다."));

        QuizDetailAdminDto dto = new QuizDetailAdminDto();
        dto.setQuizId(quiz.getId());
        dto.setQuestion(quiz.getQuestion());
        dto.setAnswer(quiz.getAnswer());
        dto.setExplanation(quiz.getExplanation());

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "랜덤 퀴즈 조회", description = "특정 레벨의 랜덤 퀴즈를 조회합니다")
    @GetMapping("/random")
    public ResponseEntity<List<Quiz>> getRandomQuizzes(
            @Parameter(description = "퀴즈 레벨", required = true) @RequestParam("level") String level) {
        List<Quiz> quizzes = quizService.getRandomQuizzesByLevel(level);
        return ResponseEntity.ok(quizzes);
    }
}
