package com.moneykidsback.model.dto.response;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WorksheetDetailResponseDto {
    private int id;
    private int difficulty;
    private String title;
    private String content;

    public WorksheetDetailResponseDto() {}

}