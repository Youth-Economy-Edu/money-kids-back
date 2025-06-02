package com.moneykidsback.model.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

// wishlist 엔티티의 복합 키 클래스
@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class UserFavoriteStockId implements Serializable {
    private int userId;
    private int stockId;
}