package com.moneykidsback.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`USER`")
public class User {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "points")
    private int points;

    @Column(name = "tendency", nullable = true)
    private String tendency;
}