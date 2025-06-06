package com.moneykidsback.controller;

import com.moneykidsback.model.dto.request.QuizCreateRequestDto;
import com.moneykidsback.model.dto.request.QuizUpdateRequestDto;
import com.moneykidsback.model.dto.response.QuizDetailAdminDto;
import com.moneykidsback.model.dto.response.QuizListAdminDto;
import com.moneykidsback.model.entity.Quiz;
import com.moneykidsback.repository.QuizRepository;
import com.moneykidsback.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/quizzes")
public class AdminQuizController {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuizService quizService;


    // ✅ 1. 퀴즈 등록 (중복 방지)
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
    @PutMapping("/{quizId}")
    public ResponseEntity<String> updateQuiz(@PathVariable int quizId,
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
    @DeleteMapping("/{quizId}")
    public ResponseEntity<String> deleteQuiz(@PathVariable int quizId) {
        if (!quizRepository.existsById(quizId)) {
            return ResponseEntity.notFound().build();
        }

        quizRepository.deleteById(quizId);
        return ResponseEntity.ok("퀴즈가 삭제되었습니다.");
    }

    // ✅ 4. 퀴즈 목록 조회 (레벨별)
    @GetMapping("/list")
    public ResponseEntity<List<QuizListAdminDto>> getQuizList(@RequestParam("level") String level) {
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
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDetailAdminDto> getQuizDetail(@PathVariable int quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("퀴즈를 찾을 수 없습니다."));

        QuizDetailAdminDto dto = new QuizDetailAdminDto();
        dto.setQuizId(quiz.getId());
        dto.setQuestion(quiz.getQuestion());
        dto.setAnswer(quiz.getAnswer());
        dto.setExplanation(quiz.getExplanation());

        return ResponseEntity.ok(dto);
    }
    @GetMapping("/random")
    public ResponseEntity<List<Quiz>> getRandomQuizzes(@RequestParam("level") String level) {
        List<Quiz> quizzes = quizService.getRandomQuizzesByLevel(level);
        return ResponseEntity.ok(quizzes);
    }
}
