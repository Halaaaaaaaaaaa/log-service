package com.tisquare.petcare.log.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeaderDto {

    private String timestamp;

    private Source source;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Source {
        private String service;  // 마이크로서비스명 또는 Task명
        private String host;     // 호스트 명
        private String ip;       // IP 주소
    }
}
