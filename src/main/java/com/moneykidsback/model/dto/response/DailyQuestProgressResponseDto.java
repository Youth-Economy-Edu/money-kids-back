package com.moneykidsback.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyQuestProgressResponseDto {
    private String userId;
    private String questType;
    private int progress;
    private int target;
    private boolean completed;
    private int reward;
}
