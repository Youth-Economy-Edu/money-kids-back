package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.QuizSubmissionRequestDto;
import com.moneykidsback.model.dto.response.QuizResultResponseDto;
import com.moneykidsback.model.entity.*;
import com.moneykidsback.repository.*;
import jakarta.transaction.Transactional;
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

    // 🟢 퀴즈 제출 메서드
    @Transactional
    public void submitQuiz(QuizSubmissionRequestDto dto) {
        // 사용자 조회
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 퀴즈 조회
        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new RuntimeException("퀴즈를 찾을 수 없습니다."));

        // 유저 퀴즈 이력 생성
        UserQuiz userQuiz = new UserQuiz();
        userQuiz.setUser(user);
        userQuiz.setQuiz(quiz);
        userQuiz.setSolveDate(LocalDate.now().toString());
        userQuiz.setCorrect(dto.getCorrect());
        userQuiz.setPoints(dto.getPoints());

        // 저장
        userQuizRepository.save(userQuiz);

        // 정답일 경우 포인트 추가
        if (dto.getCorrect()) {
            int oldPoints = user.getPoints();
            int newPoints = oldPoints + dto.getPoints();
            user.setPoints(newPoints);
            userRepository.save(user); // 🔥 update 유도

            // 디버깅 로그
            System.out.println("정답 제출! 포인트 추가됨 ✅");
            System.out.println("기존 포인트: " + oldPoints);
            System.out.println("획득 포인트: " + dto.getPoints());
            System.out.println("총 포인트: " + user.getPoints());
        } else {
            System.out.println("오답 제출. 포인트 변화 없음 ❌");
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

    // 🟢 난이도별 퀴즈 랜덤 조회
    public List<Quiz> getRandomQuizzesByLevel(String level) {
        return quizRepository.findRandomQuizzesByLevel(level);
    }
}
