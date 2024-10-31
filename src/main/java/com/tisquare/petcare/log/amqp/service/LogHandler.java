package com.tisquare.petcare.log.amqp.service;

import com.tisquare.petcare.log.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


/**
 * 로그 처리 클래스
 */
@Service
public class LogHandler {

    private final Logger customerLogger = LoggerFactory.getLogger("customer-event-logger");
    private final Logger operatorLogger = LoggerFactory.getLogger("operator-event-logger");
    private final Logger roleLogger = LoggerFactory.getLogger("role-mgmt-logger");
    private final Logger generalLogger = LoggerFactory.getLogger("general-logger");

    public void handleCustomerEvent(LogDto<?> message) {
        try {
            CustomerEventDto customerEventDto = (CustomerEventDto) message.getData();

            String logMessage = buildCustomerLogMessage(message.getHeader(), customerEventDto);
            customerLogger.info(logMessage);
        } catch (Exception e) {
            customerLogger.error("Error Customer event: {}", e.getMessage());
            generalLogger.error("Error Customer event: {}", e.getMessage());
        }
    }

    public void handleOperatorEvent(LogDto<?> message) {
        try {
            OperatorEventDto eventDto = (OperatorEventDto) message.getData();
            String logMessage = buildOperatorLogMessage(message.getHeader(), eventDto);
            operatorLogger.info(logMessage);
        } catch (Exception e) {
            operatorLogger.error("Error Operator event: {}", e.getMessage());
            generalLogger.error("Error Operator event: {}", e.getMessage());
        }

    }

    public void handleOperatorPrivacyEvent(LogDto<?> message, String routingKey) {
        try {
            OperatorEventDto eventDto = (OperatorEventDto) message.getData();
            String action = determineByRoutingKey(routingKey);

            // 권한 관리 로그
            String roleLogMessage = buildRoleLogMessage(message.getHeader(), eventDto, action);
            roleLogger.info(roleLogMessage);

            // 운영자 이벤트 로그
            String operatorLogMessage = buildOperatorLogMessage(message.getHeader(), eventDto);
            operatorLogger.info(operatorLogMessage);
        } catch (Exception e) {
            operatorLogger.error("Error Operator Role event: {}", e.getMessage());
            roleLogger.error("Error Operator Role event: {}", e.getMessage());
            generalLogger.error("Error Operator Role event: {}", e.getMessage());
        }

    }

    public void handleGeneralLog(LogDto<?> message) {
        try {
//            String logMessage = objectMapper.writeValueAsString(message);
//            generalLogger.info(logMessage);
            GeneralLogDto generalLogDto = (GeneralLogDto) message.getData();
            String logMessage = buildGeneralLogMessage(message.getHeader(), generalLogDto);
            generalLogger.info(logMessage);
        } catch (Exception e) {
            generalLogger.error("Error General log: {}", e.getMessage());
        }
    }

    private String buildCustomerLogMessage(HeaderDto header, CustomerEventDto event) {
        return String.format(
                //이벤트발생시간 |고객아이디 |고객이름 |고객ip |고객url |customData |메시지
                //"%s |%s |%s |%s |%s |%s |%s",
                //이벤트발생시간 |고객아이디 |고객이름 |고객url |customData |메시지
                "%s |%s |%s |%s | %s %s",
                header.getTimestamp(),
                event.getCustomer().getId(),
                event.getEvent().getName(),
//                event.getCustomer().getIp(),
                event.getUrl(),
//                event.getEvent().getCustomData() != null ? event.getEvent().getCustomData().toString() : "",
                event.getEvent().getCustomData() != null && !event.getEvent().getCustomData().isEmpty()
                        ? event.getEvent().getCustomData().entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining(", "))
                        : "",
//                event.getEvent().getResMessage() != null ? event.getEvent().getStatus() + " " + event.getEvent().getResMessage() : ""
                event.getEvent().getStatus() != null && event.getEvent().getResMessage() != null
                        && !("SUCCESS".equals(event.getEvent().getStatus()) && "성공".equals(event.getEvent().getResMessage()))
                        ? event.getEvent().getStatus() + " " + event.getEvent().getResMessage()
                        : ""
        );
    }

    private String buildOperatorLogMessage(HeaderDto header, OperatorEventDto event) {
        return String.format(
                //이벤트 발생 시간 |어드민 계정이름(타입) |이벤트 명 |customData |대상 이름(타입) |상태코드 메시지
                "%s |%s(%s) |%s |%s |%s(%s) |%s",
                header.getTimestamp(),
                event.getOperator().getName(),
                event.getOperator().getType(),
                event.getEvent().getName(),
                event.getEvent().getCustomData() != null ? event.getEvent().getCustomData().toString() : "",
                event.getSecurity().getTarget() != null ? event.getSecurity().getTarget() : "",
                event.getSecurity().getTargetType() != null ? event.getSecurity().getTargetType() : "",
                event.getEvent().getResMessage() != null ? event.getEvent().getStatus() + " " + event.getEvent().getResMessage() : ""

                //2024-09-23 17:43:30.123 |홍길동(KT ADMIN) |장묘 지역 등록 |{regionName=아아오오우우} | | |SUCCESS 성공
                //2024-09-23 17:43:30.123 | 홍길동(KT ADMIN) | 장묘 지역 등록 | regionName=아아오오우우 | 성공 | 200 | 123ms | http://example.com
        );
    }

    private String buildRoleLogMessage(HeaderDto header, OperatorEventDto event, String action) {
        return String.format(
                //이벤트 발생 시간 |권한부여or회수 |KT Admin 이름(계정 타입) |대상(계정 타입) |성공 여부
                "%s |%s |%s(%s) -> %s(%s) |%s",
                header.getTimestamp(),
                action,
                event.getOperator().getName(),
                event.getOperator().getType(),
                event.getSecurity().getTarget(),
                event.getSecurity().getTargetType(),
                event.getEvent().getResMessage() != null ? event.getEvent().getStatus() + " " + event.getEvent().getResMessage() : ""
        );
    }

    private String determineByRoutingKey(String routingKey) {
        return routingKey.equals("operator.privacy.created") ? "권한 부여" : "권한 회수";
    }

    private String buildGeneralLogMessage(HeaderDto header, GeneralLogDto event) {
        return String.format(
                // 이벤트 발생 시간(20자리) | level(3자리) | 이벤트 발생 서비스명(20자리) | host | 식별자(key) - msg
                "[%-20s] [%-3s] [%-20s] [%s] [%s] - %s",
                header.getTimestamp(),
                event.getLevel(),
                header.getSource().getService(),
                header.getSource().getHost(),
                event.getKey(),
                event.getMsg()
        );
    }

}

