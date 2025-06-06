package com.moneykidsback.model.dto.request;

import lombok.Getter;

@Getter
public class WorksheetRequestDto {

    private int difficulty;
    private String title;
    private String content;

    public WorksheetRequestDto() {
        // 기본 생성자 (JSON → 객체 변환할 때 필요)
    }

}

