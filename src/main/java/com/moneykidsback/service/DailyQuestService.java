package com.moneykidsback.service;

import com.moneykidsback.model.*;
import com.moneykidsback.model.dto.response.DailyQuestProgressResponseDto;
import com.moneykidsback.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DailyQuestService {

    private final DailyQuestRepository questRepository;
    private final UserRepository userRepository;

    private final String[] QUEST_TYPES = {"QUIZ", "TRADE_COUNT", "PROFIT", "BUY_STOCK", "BUY_QUANTITY", "SELL_QUANTITY"};

    // 하루에 퀘스트 3개 생성
    public void generateDailyQuests(String userId) {
        User user = userRepository.findById(userId).orElseThrow();
        if (!questRepository.findByUserAndQuestDate(user, LocalDate.now()).isEmpty()) return;

        Random random = new Random();

        for (int i = 0; i < 3; i++) {
            String type = QUEST_TYPES[random.nextInt(QUEST_TYPES.length)];
            int target = switch (type) {
                case "QUIZ" -> random.nextInt(5) + 1;
                case "TRADE_COUNT" -> random.nextInt(3) + 1;
                case "PROFIT" -> (random.nextInt(2) + 1) * 500;
                case "BUY_STOCK" -> random.nextInt(3) + 1;
                case "BUY_QUANTITY", "SELL_QUANTITY" -> random.nextInt(5) + 1;
                default -> 1;
            };

            DailyQuest quest = DailyQuest.builder()
                    .user(user)
                    .questType(type)
                    .target(target)
                    .progress(0)
                    .completed(false)
                    .questDate(LocalDate.now())
                    .build();

            questRepository.save(quest);
        }
    }

    // 퀘스트 진행 업데이트 + 완료 시 포인트 지급
    public DailyQuestProgressResponseDto updateProgress(String userId, String questType, int amount) {
        User user = userRepository.findById(userId).orElseThrow();
        List<DailyQuest> quests = questRepository.findByUserAndQuestDate(user, LocalDate.now());

        for (DailyQuest quest : quests) {
            if (quest.getQuestType().equals(questType) && !quest.isCompleted()) {
                quest.setProgress(quest.getProgress() + amount);

                int reward = 0;
                if (quest.getProgress() >= quest.getTarget()) {
                    quest.setCompleted(true);
                    reward = calculateReward(quest.getQuestType(), quest.getTarget());
                    user.setPoints(user.getPoints() + reward);
                    userRepository.save(user);
                }

                questRepository.save(quest);

                return new DailyQuestProgressResponseDto(
                        userId,
                        questType,
                        quest.getProgress(),
                        quest.getTarget(),
                        quest.isCompleted(),
                        reward
                );
            }
        }

        // 완료된 퀘스트이거나 없는 경우
        return new DailyQuestProgressResponseDto(userId, questType, 0, 0, false, 0);
    }

    private int calculateReward(String type, int target) {
        return switch (type) {
            case "QUIZ" -> 50 * target;
            case "TRADE_COUNT" -> 100 * target;
            case "PROFIT" -> target / 2;
            case "BUY_STOCK" -> 100 * target;
            case "BUY_QUANTITY", "SELL_QUANTITY" -> 30 * target;
            default -> 100;
        };
    }
}
