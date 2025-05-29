package com.moneykidsback.model.dto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "STOCK")
public class Stock {
    @Id
    private String id;

    private String name;
    private int price;
    private String category;
}
