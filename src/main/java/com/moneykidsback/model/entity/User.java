package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "USER")
public class User {
    @Id
    private String id;

    private String password;
    private String name;
    private int points;
    private String tendency;
}
