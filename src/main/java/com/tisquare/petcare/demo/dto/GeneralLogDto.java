package com.tisquare.petcare.demo.dto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralLogDto {
    @NotNull
    private String key;     // 로그 조회 키

    @NotNull
    private String level;   // 로그 레벨

    private String msg;     // 로그 메시지


    /*private Data data;     // 전송할 데이터

    // 내부 클래스로 data 정의
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private String key;     // 로그 조회 키
        private String level;   // 로그 레벨
        private String msg;     // 로그 메시지
    }*/
}
