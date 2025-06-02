package com.moneykidsback.model.dto.response;

import lombok.Getter;

@Getter
public class WorksheetResponseDto {
    private int id;
    private String title;

    public WorksheetResponseDto(int id, String title) {
        this.id = id;
        this.title = title;
    }

}
