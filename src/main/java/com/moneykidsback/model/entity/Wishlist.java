package com.moneykidsback.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Wishlist { //todo: 위시리스트 테이블이 필요함
    @EmbeddedId
    private UserFavoriteStockId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("stockId")
    @JoinColumn(name = "stock_id")
    private Stock stockID;

}