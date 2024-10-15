package com.tisquare.petcare.log.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperatorEventDto {

    private Operator operator;   // 운영자 정보
    private String url;          // 사용자 접속 url 정보
    private Event event;         // 이벤트 처리 응답 정보
    private Security security;   // 보안 탐지 정보

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Operator {
        private String id;         // 운영자 ID
        private String name;       // 운영자 이름
        private String division;   // 운영자 소속 (KT, 한라카트 등)
        private String type;       // 운영자 유형 (ADMIN, PARTNER_MANAGER 등)
        private String ip;         // 접속한 클라이언트 IP 주소
        private String agent;      // 사용한 Agent (예: firefox 등)
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Event {
        private String name;       // 이벤트 이름 (예: 관리자 생성 등)
        private String status;     // 이벤트 처리 성공 여부 (SUCCESS, FAIL)
        private String resCode;    // 결과 코드 (예: 2xx, 4xx, 5xx, ERR_4001 등)
        private String resMessage; // 결과 메시지 (예: 처리 성공, 오류 메시지 등)
        private String duration;   // 응답 시간 (밀리초 단위)
        private Map<String, Object> customData; // 각 이벤트별 정의된 JSON 데이터 (String으로 처리)
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Security {
        private String target;       // 대상 ID (CUSTOMER, ADMIN 등)
        private String targetType;   // 대상 유형 (CUSTOMER, ADMIN, PARTNER_MANAGER 등)
    }
}
