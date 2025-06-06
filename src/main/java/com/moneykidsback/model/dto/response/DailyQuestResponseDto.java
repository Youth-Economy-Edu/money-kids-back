package com.moneykidsback.model.dto.response;

import com.moneykidsback.model.entity.DailyQuest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DailyQuestResponseDto {
    private String userId;
    private List<DailyQuest> quests;
}
