package com.tisquare.petcare.log.dto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralLogDto {
    private String key;     // 로그 조회 키
    private String level;   // 로그 레벨
    private String msg;     // 로그 메시지

}
