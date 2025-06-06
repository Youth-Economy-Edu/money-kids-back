package com.moneykidsback.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {
    private String id;
    private String name;
    private String password;
}