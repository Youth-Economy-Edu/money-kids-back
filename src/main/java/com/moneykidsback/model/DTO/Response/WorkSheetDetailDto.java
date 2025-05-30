package com.moneykidsback.model.DTO.Response;

public class WorkSheetDetailDto {
    private int id;
    private int difficulty;
    private String title;
    private String content;

    public WorkSheetDetailDto() {}

    public int getId() { return id; }
    public int getDifficulty() { return difficulty; }
    public String getTitle() { return title; }
    public String getContent() { return content; }

    public void setId(int id) { this.id = id; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
}

