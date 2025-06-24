package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_worksheet")
public class UserWorksheet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;
    
    @Column(name = "worksheet_id", nullable = false)
    private Integer worksheetId;
    
    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;
    
    @Column(name = "points_earned", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer pointsEarned;
    
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    // 기본 생성자
    public UserWorksheet() {
        this.createdAt = LocalDateTime.now();
    }
    
    // 생성자
    public UserWorksheet(String userId, Integer worksheetId, LocalDate completionDate, Integer pointsEarned) {
        this.userId = userId;
        this.worksheetId = worksheetId;
        this.completionDate = completionDate;
        this.pointsEarned = pointsEarned;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public LocalDate getCompletionDate() {
        return completionDate;
    }
    
    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }
    
    public Integer getPointsEarned() {
        return pointsEarned;
    }
    
    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
