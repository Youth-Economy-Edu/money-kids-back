package com.moneykidsback.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;

    private String name;

    @Column(nullable = true)
    private String password; // 소셜 로그인 사용자는 null 가능

    private int points=0;
}