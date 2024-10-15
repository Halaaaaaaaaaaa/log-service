package com.tisquare.petcare.log.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralLogDto {
    private String key;     // 로그 조회 키
    private String level;   // 로그 레벨
    private String msg;     // 로그 메시지

}
