package com.moneykidsback.model.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_quiz")
public class UserQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "solve_date", length = 20)
    private String solveDate;

    @Column(name = "correct")
    private Boolean correct;

    @Column(name = "points")
    private Integer points;  // 문제 단위로 획득한 포인트

}