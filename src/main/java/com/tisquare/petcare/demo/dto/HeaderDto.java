package com.tisquare.petcare.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeaderDto {

    @NotNull(message = "Timestamp is Null")
    private String timestamp;

    @NotNull(message = "Source is Null")
    private Source source;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Source {

        @NotNull(message = "service is Null")
        private String service;  // 마이크로서비스명 또는 Task명

        @NotNull(message = "host is Null")
        private String host;     // 호스트 명

        @NotNull(message = "ip is Null")
        private String ip;       // IP 주소
    }
}
