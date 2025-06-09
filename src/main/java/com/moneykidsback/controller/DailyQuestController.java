package com.moneykidsback.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneykidsback.model.dto.request.DailyQuestProgressRequestDto;
import com.moneykidsback.model.dto.response.DailyQuestProgressResponseDto;
import com.moneykidsback.model.dto.response.DailyQuestResponseDto;
import com.moneykidsback.model.entity.DailyQuest;
import com.moneykidsback.model.entity.User;
import com.moneykidsback.repository.DailyQuestRepository;
import com.moneykidsback.repository.UserRepository;
import com.moneykidsback.service.DailyQuestService;

import lombok.RequiredArgsConstructor;

/**
 * 🎯 일일 학습 목표/퀘스트 컨트롤러
 * - 매일 자동 생성되는 학습 목표
 * - 퀴즈, 거래, 학습 진행률 추적
 * - 포인트 리워드 시스템
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class DailyQuestController {

    private final DailyQuestRepository questRepository;
    private final UserRepository userRepository;
    private final DailyQuestService questService;

    @GetMapping("/{userId}/quests")
    public DailyQuestResponseDto getDailyQuests(@PathVariable String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<DailyQuest> quests = questRepository.findByUserAndQuestDate(user, LocalDate.now());

        if (quests.isEmpty()) {
            questService.generateDailyQuests(userId);
            quests = questRepository.findByUserAndQuestDate(user, LocalDate.now());
        }

        return new DailyQuestResponseDto(userId, quests.stream().limit(3).toList());
    }

    @PostMapping("/{userId}/quests/progress")
    public ResponseEntity<DailyQuestProgressResponseDto> updateQuestProgress(
            @PathVariable String userId,
            @RequestBody DailyQuestProgressRequestDto request
    ) {
        var response = questService.updateProgress(userId, request.getQuestType(), request.getAmount());
        return ResponseEntity.ok(response);
    }
}