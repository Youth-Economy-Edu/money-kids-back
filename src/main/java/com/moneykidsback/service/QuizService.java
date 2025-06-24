package com.moneykidsback.service;

import com.moneykidsback.model.dto.request.QuizSubmissionRequestDto;
import com.moneykidsback.model.dto.response.QuizResultResponseDto;
import com.moneykidsback.model.entity.*;
import com.moneykidsback.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserQuizRepository userQuizRepository;

    @Autowired
    private UserQuizSessionRepository userQuizSessionRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

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

    // 🟢 퀴즈 세션 완료 및 포인트 지급 (24시간 제한)
    @Transactional
    public java.util.Map<String, Object> completeQuizSession(String userId, Integer quizLevel, Integer totalQuestions, Integer correctAnswers) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 오늘 날짜
        String today = LocalDate.now().toString();
        
        // 24시간 제한 체크: 같은 레벨의 퀴즈를 오늘 이미 완료했는지 확인
        java.util.Optional<UserQuizSession> existingSession = userQuizSessionRepository
                .findByUserAndQuizLevelAndCompletionDate(user, quizLevel, today);
        
        if (existingSession.isPresent()) {
            // 이미 오늘 완료한 경우
            return java.util.Map.of(
                "pointsAwarded", false,
                "pointsEarned", 0,
                "message", "오늘 이미 이 난이도의 퀴즈를 완료하셨습니다. 내일 다시 도전해주세요!",
                "correctAnswers", correctAnswers,
                "totalQuestions", totalQuestions,
                "percentage", Math.round((double) correctAnswers / totalQuestions * 100)
            );
        }
        
        // 포인트 계산 (정답률 기반, 난이도별 차등)
        int pointsEarned = calculateQuizPoints(correctAnswers, totalQuestions, quizLevel);
        
        // 퀴즈 세션 기록 생성
        UserQuizSession session = new UserQuizSession();
        session.setUser(user);
        session.setQuizLevel(quizLevel);
        session.setTotalQuestions(totalQuestions);
        session.setCorrectAnswers(correctAnswers);
        session.setPointsEarned(pointsEarned);
        session.setCompletionDate(today);
        session.setCompletedAt(java.time.LocalDateTime.now());
        session.setPointsAwarded(pointsEarned > 0);
        
        // 세션 저장
        userQuizSessionRepository.save(session);
        
        // ActivityLog 저장
        ActivityLog activityLog = ActivityLog.builder()
                .userId(userId)
                .activityType("QUIZ")
                .userQuizId(session.getId())
                .quizLevel(String.valueOf(quizLevel))
                .quizCategory("경제") // 실제로는 퀴즈 카테고리를 동적으로 설정
                .status("SUCCESS")
                .build();
        activityLogRepository.save(activityLog);
        
        // 포인트 지급
        if (pointsEarned > 0) {
            int oldPoints = user.getPoints();
            user.setPoints(oldPoints + pointsEarned);
            userRepository.save(user);
            
            System.out.println("퀴즈 세션 완료! 포인트 지급됨 ✅");
            System.out.println("기존 포인트: " + oldPoints);
            System.out.println("획득 포인트: " + pointsEarned);
            System.out.println("총 포인트: " + user.getPoints());
        }
        
        return java.util.Map.of(
            "pointsAwarded", pointsEarned > 0,
            "pointsEarned", pointsEarned,
            "message", pointsEarned > 0 ? 
                "축하합니다! " + pointsEarned + "포인트를 획득했습니다!" : 
                "퀴즈를 완료했습니다. 더 많은 문제를 맞혀서 포인트를 획득해보세요!",
            "correctAnswers", correctAnswers,
            "totalQuestions", totalQuestions,
            "percentage", Math.round((double) correctAnswers / totalQuestions * 100)
        );
    }
    
    // 🟢 퀴즈 포인트 계산 (난이도별 차등 지급)
    private int calculateQuizPoints(int correctAnswers, int totalQuestions, int quizLevel) {
        if (totalQuestions == 0) return 0;
        
        double percentage = (double) correctAnswers / totalQuestions;
        
        // 난이도별 기본 포인트 설정
        int basePoints = getBasePointsByLevel(quizLevel);
        
        // 정답률에 따른 포인트 지급 (기본 포인트의 배수)
        if (percentage >= 0.9) {
            return (int)(basePoints * 1.5);  // 90% 이상: 기본 포인트의 1.5배
        } else if (percentage >= 0.7) {
            return (int)(basePoints * 1.2);  // 70% 이상: 기본 포인트의 1.2배
        } else if (percentage >= 0.5) {
            return basePoints;               // 50% 이상: 기본 포인트
        } else {
            return 0;                        // 50% 미만: 포인트 없음
        }
    }
    
    // 🟢 난이도별 기본 포인트 반환
    private int getBasePointsByLevel(int level) {
        switch (level) {
            case 1: return 1000;  // 기초: 1000포인트
            case 2: return 1500;  // 초급: 1500포인트
            case 3: return 2000;  // 중급: 2000포인트
            case 4: return 2500;  // 고급: 2500포인트
            case 5: return 3000;  // 전문가: 3000포인트
            default: return 1000; // 기본값
        }
    }
    
    // 🟢 사용자 퀴즈 진행 현황 조회
    public java.util.Map<String, Object> getUserQuizProgress(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        String today = LocalDate.now().toString();
        
        // 오늘 완료한 퀴즈 세션들 조회
        List<UserQuizSession> todaySessions = userQuizSessionRepository
                .findByUserOrderByCompletedAtDesc(user)
                .stream()
                .filter(session -> session.getCompletionDate().equals(today))
                .collect(Collectors.toList());
        
        // 레벨별 완료 상태 확인
        java.util.Map<Integer, Boolean> levelCompletionStatus = new java.util.HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int level = i;
            boolean completed = todaySessions.stream()
                    .anyMatch(session -> session.getQuizLevel().equals(level));
            levelCompletionStatus.put(level, completed);
        }
        
        return java.util.Map.of(
            "todayCompletedLevels", levelCompletionStatus,
            "todaySessionsCount", todaySessions.size(),
            "totalPointsEarnedToday", todaySessions.stream()
                    .mapToInt(UserQuizSession::getPointsEarned)
                    .sum(),
            "recentSessions", todaySessions.stream()
                    .limit(10)
                    .collect(Collectors.toList())
        );
    }
}
