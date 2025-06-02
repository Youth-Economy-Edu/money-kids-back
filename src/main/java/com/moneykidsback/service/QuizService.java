package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.QuizSubmissionRequestDto;
import com.moneykidsback.model.dto.response.QuizResultResponseDto;
import com.moneykidsback.model.entity.*;
import com.moneykidsback.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserQuizRepository userQuizRepository;

    // 🟢 퀴즈 제출
    public void submitQuiz(QuizSubmissionRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new RuntimeException("퀴즈를 찾을 수 없습니다."));

        UserQuiz userQuiz = new UserQuiz();
        userQuiz.setUser(user);
        userQuiz.setQuiz(quiz);
        userQuiz.setSolveDate(LocalDate.now().toString());
        userQuiz.setCorrect(dto.isCorrect());
        userQuiz.setPoints(dto.getPoints());

        userQuizRepository.save(userQuiz);

        if (dto.isCorrect()) {
            int updatedPoints = user.getPoints() + dto.getPoints();
            user.setPoints(updatedPoints);
            userRepository.save(user);
        }
    }

    // 🟢 퀴즈 결과 조회
    public List<QuizResultResponseDto> getUserQuizResults(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<UserQuiz> userQuizList = userQuizRepository.findByUser(user);

        return userQuizList.stream().map(userQuiz -> {
            QuizResultResponseDto dto = new QuizResultResponseDto();
            dto.setQuizId(userQuiz.getQuiz().getId());
            dto.setCorrect(Boolean.TRUE.equals(userQuiz.getCorrect()));
            dto.setPoints(userQuiz.getPoints());
            dto.setSolveDate(userQuiz.getSolveDate());
            return dto;
        }).collect(Collectors.toList());
    }
    public List<Quiz> getRandomQuizzesByLevel(String level) {
        return quizRepository.findRandomQuizzesByLevel(level);
    }
}
