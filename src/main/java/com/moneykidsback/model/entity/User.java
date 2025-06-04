package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@DynamicUpdate
@Getter
@Setter
@Table(name = "user")  // user는 예약어라 반드시 명시!
public class User {

    @Id
    @Column(length = 50)
    private String id;  // VARCHAR(50) PK

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 255)
    private String name;

    @Column
    private int points;

    @Column(length = 100)
    private String tendency;
}

