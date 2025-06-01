package com.moneykidsback.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NewSaveDto {
    public String effect;  // 영향
    public String title;   // 제목
    public String body;    // 본문
}
