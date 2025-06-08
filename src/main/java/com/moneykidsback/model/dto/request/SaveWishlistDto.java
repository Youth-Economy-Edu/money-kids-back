package com.moneykidsback.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SaveWishlistDto {
    private String userId;
    private String stockId;
}
