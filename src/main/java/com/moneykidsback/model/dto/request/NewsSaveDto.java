package com.moneykidsback.model.dto.request;

import com.moneykidsback.model.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsSaveDto {
    private Stock stockId; // 주식 ID
    private String effect;  // 영향
    private String title;   // 제목
    private String content;    // 본문

    public NewsSaveDto(String effect, String title, String content) {
        this.effect = effect;
        this.title = title;
        this.content = content;
    }
}
