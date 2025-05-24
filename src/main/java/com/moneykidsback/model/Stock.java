package com.moneykidsback.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "stock")
public class Stock {
    @Id
    private String code;
    @Column()
    private String name;
    @Column
    private int price = 0;
    @Column
    private String category;
}
