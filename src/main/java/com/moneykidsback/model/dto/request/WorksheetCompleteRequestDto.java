package com.moneykidsback.model.dto.request;

public class WorksheetCompleteRequestDto {
    private String userId;
    private Integer worksheetId;
    private Integer pointsEarned;
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public Integer getWorksheetId() {
        return worksheetId;
    }
    
    public void setWorksheetId(Integer worksheetId) {
        this.worksheetId = worksheetId;
    }
    
    public Integer getPointsEarned() {
        return pointsEarned;
    }
    
    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
}
