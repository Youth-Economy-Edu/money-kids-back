package com.moneykidsback.service;

import com.moneykidsback.model.dto.response.WorksheetDetailResponseDto;
import com.moneykidsback.model.dto.response.WorksheetResponseDto;
import com.moneykidsback.model.entity.ActivityLog;
import com.moneykidsback.model.entity.User;
import com.moneykidsback.model.entity.UserWorksheet;
import com.moneykidsback.model.entity.Worksheet;
import com.moneykidsback.repository.ActivityLogRepository;
import com.moneykidsback.repository.UserRepository;
import com.moneykidsback.repository.UserWorksheetRepository;
import com.moneykidsback.repository.WorksheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorksheetService {

    private final WorksheetRepository worksheetRepository;
    private final UserWorksheetRepository userWorksheetRepository;
    private final UserRepository userRepository;
    private final ActivityLogRepository activityLogRepository;

    // 목록 조회
    public List<WorksheetResponseDto> getConceptsByDifficulty(int level) {
        return worksheetRepository.findByDifficulty(level).stream()
                .map(ws -> new WorksheetResponseDto(ws.getId(), ws.getTitle()))
                .collect(Collectors.toList());
    }

    // 단건 조회
    public WorksheetDetailResponseDto getConceptById(Integer conceptId) {
        Optional<Worksheet> optionalWorksheet = worksheetRepository.findById(conceptId);
        if (optionalWorksheet.isPresent()) {
            Worksheet ws = optionalWorksheet.get();
            WorksheetDetailResponseDto dto = new WorksheetDetailResponseDto();
            dto.setId(ws.getId());
            dto.setDifficulty(ws.getDifficulty());
            dto.setTitle(ws.getTitle());
            dto.setContent(ws.getContent());
            return dto;
        }
        return null;
    }
    
    /**
     * 학습 완료 처리 및 포인트 지급 (난이도별 차등 지급)
     * @param userId 사용자 ID
     * @param worksheetId 학습지 ID
     * @param pointsToAward 지급할 포인트 (사용하지 않음, 난이도별로 자동 계산)
     * @return 결과 정보
     */
    @Transactional
    public Map<String, Object> completeWorksheet(String userId, Integer worksheetId, Integer pointsToAward) {
        Map<String, Object> result = new HashMap<>();
        LocalDate today = LocalDate.now();
        
        // 학습지 정보 조회하여 난이도 확인
        Worksheet worksheet = worksheetRepository.findById(worksheetId)
            .orElseThrow(() -> new IllegalArgumentException("학습지를 찾을 수 없습니다: " + worksheetId));
        
        // 난이도별 포인트 계산
        int actualPointsToAward = calculateWorksheetPoints(worksheet.getDifficulty());
        
        // 이 학습지를 이전에 완료한 적이 있는지 확인
        Optional<UserWorksheet> anyPreviousCompletion = userWorksheetRepository
            .findFirstByUserIdAndWorksheetIdOrderByCompletionDateDesc(userId, worksheetId);
        
        // 오늘 이미 완료했는지 확인
        Optional<UserWorksheet> todayCompletion = userWorksheetRepository
            .findByUserIdAndWorksheetIdAndCompletionDate(userId, worksheetId, today);
        
        if (todayCompletion.isPresent()) {
            // 이미 오늘 완료한 경우 - 포인트 지급 없음
            result.put("pointsAwarded", false);
            result.put("pointsEarned", 0);
            result.put("isFirstTime", false);
            result.put("isRelearning", true);
            result.put("message", "오늘은 이미 완료하셨습니다.");
            return result;
        }
        
        boolean isFirstTime = !anyPreviousCompletion.isPresent();
        
        // 새로운 완료 기록 생성 (실제 계산된 포인트 사용)
        UserWorksheet userWorksheet = new UserWorksheet(userId, worksheetId, today, actualPointsToAward);
        userWorksheetRepository.save(userWorksheet);
        
        // 사용자 포인트 업데이트
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        
        user.setPoints(user.getPoints() + actualPointsToAward);
        userRepository.save(user);
        
        // ActivityLog 저장
        ActivityLog activityLog = ActivityLog.builder()
                .userId(userId)
                .activityType("CONTENT_COMPLETION")
                .completionId(userWorksheet.getId())
                .worksheetDifficulty(String.valueOf(worksheet.getDifficulty()))
                .worksheetCategory("경제학습")
                .status("SUCCESS")
                .build();
        activityLogRepository.save(activityLog);
        
        result.put("pointsAwarded", true);
        result.put("pointsEarned", actualPointsToAward);
        result.put("totalPoints", user.getPoints());
        result.put("isFirstTime", isFirstTime);
        result.put("isRelearning", !isFirstTime);
        
        if (isFirstTime) {
            result.put("message", "첫 학습 완료! " + actualPointsToAward + "포인트가 지급되었습니다.");
        } else {
            result.put("message", "재학습 완료! " + actualPointsToAward + "포인트가 지급되었습니다.");
        }
        
        return result;
    }
    
    /**
     * 학습지 난이도별 포인트 계산
     * @param difficulty 난이도 (1: 기초, 2: 초급, 3: 중급, 4: 고급, 5: 전문가)
     * @return 지급할 포인트
     */
    private int calculateWorksheetPoints(int difficulty) {
        switch (difficulty) {
            case 1: return 800;   // 기초: 800포인트
            case 2: return 1200;  // 초급: 1200포인트
            case 3: return 1600;  // 중급: 1600포인트
            case 4: return 2000;  // 고급: 2000포인트
            case 5: return 2400;  // 전문가: 2400포인트
            default: return 800;  // 기본값
        }
    }
    
    /**
     * 사용자 학습 진도 조회
     * @param userId 사용자 ID
     * @return 완료한 학습지 ID와 완료 여부 맵
     */
    @Transactional(readOnly = true)
    public Map<Integer, Boolean> getUserProgress(String userId) {
        List<Integer> completedWorksheetIds = userWorksheetRepository
            .findCompletedWorksheetIdsByUserId(userId);
        
        Map<Integer, Boolean> progress = new HashMap<>();
        for (Integer worksheetId : completedWorksheetIds) {
            progress.put(worksheetId, true);
        }
        
        return progress;
    }
}

