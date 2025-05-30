package com.moneykidsback.model.DTO.Response;

public class WorkSheetResponseDto {
    private int id;
    private String title;

    public WorkSheetResponseDto(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
