package com.moneykidsback.controller;

import com.moneykidsback.model.DailyQuest;
import com.moneykidsback.model.User;
import com.moneykidsback.model.dto.request.DailyQuestProgressRequestDto;
import com.moneykidsback.model.dto.response.DailyQuestProgressResponseDto;
import com.moneykidsback.model.dto.response.DailyQuestResponseDto;
import com.moneykidsback.repository.DailyQuestRepository;
import com.moneykidsback.repository.UserRepository;
import com.moneykidsback.service.DailyQuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

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