package com.tisquare.petcare.log.dto;

import com.tisquare.petcare.log.validation.ValidatorGroup.HeaderGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeaderDto {

    @NotBlank(groups = {HeaderGroup.class}, message = "timestamp 누락")
    private String timestamp;

    @NotNull(groups = {HeaderGroup.class}, message = "source 누락")
    private Source source;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Source {
        @NotBlank(groups = {HeaderGroup.class}, message = "service 누락")
        private String service;  // 마이크로서비스명 또는 Task명

        @NotBlank(groups = {HeaderGroup.class}, message = "host 누락")
        private String host;     // 호스트 명

        @NotBlank(groups = {HeaderGroup.class}, message = "ip 누락")
        private String ip;       // IP 주소
    }
}
