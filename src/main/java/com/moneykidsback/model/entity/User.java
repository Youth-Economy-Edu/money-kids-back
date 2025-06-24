package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "`user`")  // MySQL에서 user는 예약어이므로 백틱으로 감쌉니다
public class User {

    @Id
    @Column(name = "id", length = 50) // 컬럼명 명시, 길이 50
    private String id;

    @Column(name = "password", length = 255, nullable = false) // 길이 255, null 비허용
    private String password;

    @Column(name = "name", length = 255) // 길이 255
    private String name;

    @Column(name = "points")
    private int points;

    @Column(name = "tendency", length = 100, nullable = true) // 길이 100, null 허용
    private String tendency;

    @Column(name = "email", length = 255, nullable = true)
    private String email;

    @Column(name = "provider", length = 20, nullable = true) // GOOGLE, KAKAO, LOCAL
    private String provider;
}
