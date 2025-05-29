package com.moneykidsback.model.DTO.Request;


public class WorkSheetRequestDto {

    private int difficulty;
    private String title;
    private String content;

    public WorkSheetRequestDto() {
        // 기본 생성자 (JSON → 객체 변환할 때 필요)
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
