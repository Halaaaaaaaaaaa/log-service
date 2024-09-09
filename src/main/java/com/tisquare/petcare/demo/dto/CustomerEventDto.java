package com.tisquare.petcare.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEventDto {

    @NotNull
    private Customer customer;    // 고객 정보

    @NotNull
    private String url;           // 사용자 접속 url 정보

    private Event event;          // 이벤트 상세 정보

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Customer {
        @NotNull
        private String id;         // 식별자

        @NotNull
        private String ip;         // 접속한 클라이언트 IP 주소

        @NotNull
        private String agent;      // 사용한 Agent
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Event {
        @NotNull
        private String name;       // 이름

        @NotNull
        private String status;     // 이벤트 처리 상태 (SUCCESS | FAIL)

        @NotNull
        private String resCode;    // 결과 코드 (2xx, 4xx, 5xx)

        private String resMessage; // 결과 메시지
        private String duration;   // 응답 시간 (밀리초)
        private Map<String, Object> customData; // 각 이벤트별 정의된 JSON 데이터 (String으로 처리)
    }
}
