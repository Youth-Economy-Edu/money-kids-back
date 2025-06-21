package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.QuizSubmitRequestDto; // (새로 생성될 DTO)
import com.moneykidsback.model.dto.request.UserAnswerDto; // (새로 생성될 DTO)
import com.moneykidsback.model.dto.response.QuizResultResponseDto;
import com.moneykidsback.model.dto.response.QuizSubmitResponseDto; // (새로 생성될 DTO)
import com.moneykidsback.model.entity.*;
import com.moneykidsback.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // 의존성 주입 방식을 생성자 주입으로 변경합니다.
public class QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final UserQuizRepository userQuizRepository;
    // 보상 로직을 위해 새로 추가될 Repository 의존성
    private final QuizRewardLogRepository quizRewardLogRepository;
    // 다른 서비스와의 연동을 위해 의존성 추가
    private final ActivityLogService activityLogService;
    private final DailyQuestService dailyQuestService;

    // [기존 유지] 난이도별 퀴즈 랜덤 조회 (읽기 전용 트랜잭션 추가)
    @Transactional(readOnly = true)
    public List<Quiz> getRandomQuizzesByLevel(String level) {
        return quizRepository.findRandomQuizzesByLevel(level);
    }

    // [로직 전체 변경] 퀴즈 제출, 채점, 보상, 로그 기록을 모두 처리하는 통합 메서드
    @Transactional
    public QuizSubmitResponseDto submitQuizAndProcess(String userId, QuizSubmitRequestDto submitDto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        List<Integer> quizIds = submitDto.getAnswers().stream().map(UserAnswerDto::getQuizId).collect(Collectors.toList());
        Map<Integer, Quiz> quizMap = quizRepository.findAllById(quizIds).stream()
            .collect(Collectors.toMap(Quiz::getId, quiz -> quiz));

        int correctCount = 0;

        // --- 1. 서버 사이드 채점 및 풀이 기록 저장 ---
        for (UserAnswerDto userAnswer : submitDto.getAnswers()) {
            Quiz quiz = quizMap.get(userAnswer.getQuizId());
            if (quiz == null) continue; // 존재하지 않는 퀴즈는 건너뛰기

            boolean isCorrect = quiz.getAnswer().equalsIgnoreCase(userAnswer.getUserAnswer());
            if (isCorrect) {
                correctCount++;
            }

            // 개별 풀이 기록 (user_quiz) 저장
            UserQuiz userQuizLog = new UserQuiz();
            userQuizLog.setUser(user);
            userQuizLog.setQuiz(quiz);
            userQuizLog.setSolveDate(LocalDate.now().toString());
            userQuizLog.setCorrect(isCorrect);
            userQuizLog.setPoints(0); // 개별 문제 포인트는 0으로 통일, 만점 보상만 지급
            userQuizRepository.save(userQuizLog);

            // 활동 로그 및 일일 퀘스트 업데이트
            activityLogService.logQuiz(userId, quiz.getLevel(), "경제기초", isCorrect ? "CORRECT" : "WRONG");
            dailyQuestService.updateProgress(userId, "QUIZ", 1);
        }

        // --- 2. 만점 보상 처리 ---
        int awardedPoints = 0;
        String message = String.format("퀴즈 결과: %d / %d", correctCount, submitDto.getAnswers().size());

        if (correctCount == 5) { // 5문제 만점일 경우
            LocalDate today = LocalDate.now();
            String difficulty = String.valueOf(submitDto.getDifficulty());

            boolean alreadyRewarded = quizRewardLogRepository.existsByUserAndRewardDateAndDifficulty(user, today, difficulty);

            if (!alreadyRewarded) {
                awardedPoints = difficulty.equals("0") ? 1000 : Integer.parseInt(difficulty) * 1000; // 난이도에 따른 포인트
                user.setPoints(user.getPoints() + awardedPoints);

                QuizRewardLog rewardLog = QuizRewardLog.builder()
                    .user(user)
                    .rewardDate(today)
                    .difficulty(difficulty)
                    .rewardPoints(awardedPoints)
                    .build();
                quizRewardLogRepository.save(rewardLog);

                message = "🎉축하합니다! 모든 문제를 맞혀 " + awardedPoints + "P를 획득했습니다!";
            } else {
                message = "만점이지만, 오늘 이미 보상을 받았습니다.";
            }
        }

        return QuizSubmitResponseDto.builder()
            .correctCount(correctCount)
            .totalCount(submitDto.getAnswers().size())
            .awardedPoints(awardedPoints)
            .message(message)
            .build();
    }

    // [기존 유지] 퀴즈 결과 조회
    @Transactional(readOnly = true)
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
}
