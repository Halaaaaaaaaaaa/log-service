package com.tisquare.petcare.log.amqp.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tisquare.petcare.log.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 로그 처리 클래스
 */
@Service
public class LogHandler {

    private final Logger customerLogger = LoggerFactory.getLogger("customer-event-logger");
    private final Logger operatorLogger = LoggerFactory.getLogger("operator-event-logger");
    private final Logger roleLogger = LoggerFactory.getLogger("role-mgmt-logger");
    private final Logger generalLogger = LoggerFactory.getLogger("general-logger");

    @Autowired
    private ObjectMapper objectMapper;
    public void handleCustomerEvent(LogDto<?> message) {
        try {
            String logMessage = objectMapper.writeValueAsString(message);
            customerLogger.info(logMessage);
        } catch (Exception e) {
            customerLogger.error("Error processing customer event: {}", e.getMessage());
            generalLogger.error("Error processing customer event: {}", e.getMessage());
        }
    }

    public void handleOperatorEvent(LogDto<?> message) {
        try {
            String logMessage = objectMapper.writeValueAsString(message);
            operatorLogger.info(logMessage);
        } catch (Exception e) {
            operatorLogger.error("Error processing operator event: {}", e.getMessage());
            generalLogger.error("Error processing operator event: {}", e.getMessage());
        }
    }

    public void handleOperatorPrivacyEvent(LogDto<?> message, String routingKey) {
        try {
            String logMessage = objectMapper.writeValueAsString(message);
            roleLogger.info(logMessage);
            operatorLogger.info(logMessage);
        } catch (Exception e) {
            roleLogger.error("Error processing operator privacy event: {}", e.getMessage());
            generalLogger.error("Error processing operator privacy event: {}", e.getMessage());
        }
    }

    public void handleGeneralLog(LogDto<?> message) {
        try {
            String logMessage = objectMapper.writeValueAsString(message);
            generalLogger.info(logMessage);
        } catch (Exception e) {
            generalLogger.error("Error processing general log: {}", e.getMessage());
        }
    }

    //error 로그
    public String buildErrorMessage(HeaderDto header, String messageBody, Exception e) {
        return String.format(
                // 이벤트 발생 시간 |ERROR |이벤트 발생 서비스명 |host |식별자(key) - msg
                "%s |ERROR |%s |%s |%s - %s",
                header.getTimestamp(),
                header.getSource() != null && header.getSource().getService() != null ? header.getSource().getService() : "UNKNOWN",
                header.getSource() != null && header.getSource().getHost() != null ? header.getSource().getHost() : "UNKNOWN",
                e.getMessage(),
                messageBody
        );
    }
}

/*@Service
public class LogHandler {

    private final Logger customerLogger = LoggerFactory.getLogger("customer-event-logger");
    private final Logger operatorLogger = LoggerFactory.getLogger("operator-event-logger");
    private final Logger roleLogger = LoggerFactory.getLogger("role-mgmt-logger");
    private final Logger generalLogger = LoggerFactory.getLogger("general-logger");

    public void handleCustomerEvent(LogDto<?> message) {
        CustomerEventDto eventDto = (CustomerEventDto) message.getData();
        String logMessage = buildCustomerLogMessage(message.getHeader(), eventDto);
        customerLogger.info(logMessage);
    }


    public void handleOperatorEvent(LogDto<?> message) {
        OperatorEventDto eventDto = (OperatorEventDto) message.getData();
        String logMessage = buildOperatorLogMessage(message.getHeader(), eventDto);
        operatorLogger.info(logMessage);
    }

    public void handleOperatorPrivacyEvent(LogDto<?> message, String routingKey) {
        OperatorEventDto eventDto = (OperatorEventDto) message.getData();
        String action = determineByRoutingKey(routingKey);

        //권한 이력 로그
        String roleLogMessage = buildRoleLogMessage(message.getHeader(), eventDto, action);
        roleLogger.info(roleLogMessage);

        //운영자 이벤트 로그
        String operatorLogMessage = buildOperatorLogMessage(message.getHeader(), eventDto);
        operatorLogger.info(operatorLogMessage);
    }

    public void handleGeneralLog(LogDto<?> message) {
        GeneralLogDto eventDto = (GeneralLogDto) message.getData();
        String logMessage = buildGeneralLogMessage(message.getHeader(), eventDto);
        generalLogger.info(logMessage);
    }

    private String buildCustomerLogMessage(HeaderDto header, CustomerEventDto event) {
        //이벤트 발생 시간 |식별자(고객ID) |행위(이벤트 이름) |이벤트 관련 데이터 |결과
        StringBuilder logMessageBuilder = new StringBuilder();

        logMessageBuilder.append(String.format(
                "%s |%s |%s |%s |%s",
                header.getTimestamp(),
                event.getCustomer().getId(),
                event.getEvent().getName(),
                event.getCustomer().getIp(),
                event.getUrl()

        ));

        if (event.getEvent().getCustomData() != null && !event.getEvent().getCustomData().isEmpty()) {
            logMessageBuilder.append(String.format(" |%s",
                    event.getEvent().getCustomData().toString()));
        } else {
//            logMessageBuilder.append(" |NULL");
            logMessageBuilder.append("");
        }

        if (event.getEvent().getResMessage() != null && !event.getEvent().getResMessage().isEmpty()) {
            logMessageBuilder.append(String.format(" |%s %s",
                    event.getEvent().getStatus(),
                    event.getEvent().getResMessage()));
        } else {
//            logMessageBuilder.append(" |NULL");
            logMessageBuilder.append("");
        }

        return logMessageBuilder.toString();
    }

    *//*private String buildOperatorLogMessage(HeaderDto header, OperatorEventDto event) {
        return String.format(
                //이벤트 발생 시간 |주체(운영자 이름 및 유형) |행위(이벤트 이름) |URL |이벤트 관련 데이터 |대상:security |결과
                "%s |%s(%s) |%s |%s |%s |대상: %s(%s) |%s",
                header.getTimestamp(),
                event.getOperator().getName(),
                event.getOperator().getType(),
                event.getEvent().getName(),
                event.getUrl(),
                event.getEvent().getCustomData(),
                event.getSecurity().getTarget(),
                event.getSecurity().getTargetType(),
                event.getEvent().getResMessage()
        );
    }*//*
    *//*private String buildOperatorLogMessage(HeaderDto header, OperatorEventDto event) {
        StringBuilder logMessageBuilder = new StringBuilder();

        logMessageBuilder.append(String.format(
                "%s |%s(%s) |%s |%s |%s",
                header.getTimestamp(),
                event.getOperator().getName(),
                event.getOperator().getType(),
                event.getEvent().getName(),
                event.getUrl(),
                event.getEvent().getCustomData()
        ));

        if (event.getSecurity() != null &&
                event.getSecurity().getTarget() != null && !event.getSecurity().getTarget().isEmpty() &&
                event.getSecurity().getTargetType() != null && !event.getSecurity().getTargetType().isEmpty()) {
            logMessageBuilder.append(String.format(" |대상: %s(%s)",
                    event.getSecurity().getTarget(),
                    event.getSecurity().getTargetType()));
        }

        //결과 메시지가 있을 경우만 추가
        if (event.getEvent().getResMessage() != null && !event.getEvent().getResMessage().isEmpty()) {
            logMessageBuilder.append(String.format(" |%s", event.getEvent().getResMessage()));
        }

        return logMessageBuilder.toString();
    }*//*
    private String buildOperatorLogMessage(HeaderDto header, OperatorEventDto event) {
        StringBuilder logMessageBuilder = new StringBuilder();

        logMessageBuilder.append(String.format(
                "%s |%s(%s) |%s",
                header.getTimestamp(),
                event.getOperator().getName(),
                event.getOperator().getType(),
                event.getEvent().getName()
        ));

        if (event.getEvent().getCustomData() != null && !event.getEvent().getCustomData().isEmpty()) {
            logMessageBuilder.append(String.format(" |%s",
                    event.getEvent().getCustomData().toString()));
        } else {
//            logMessageBuilder.append(" |NULL");
            logMessageBuilder.append("");
        }

        if (event.getSecurity() != null && event.getSecurity().getTarget() != null) {
            logMessageBuilder.append(String.format(" |대상: %s(%s)",
                    event.getSecurity().getTarget(),
                    event.getSecurity().getTargetType()));
        } else {
//            logMessageBuilder.append(" |NULL");
            logMessageBuilder.append("");
        }

        if (event.getEvent().getResMessage() != null && !event.getEvent().getResMessage().isEmpty()) {
            logMessageBuilder.append(String.format(" |%s %s",
                    event.getEvent().getStatus(),
                    event.getEvent().getResMessage()));
        } else {
//            logMessageBuilder.append(" |NULL");
            logMessageBuilder.append("");
        }

        return logMessageBuilder.toString();
    }

    private String buildRoleLogMessage(HeaderDto header, OperatorEventDto event, String action) {
        return String.format(
                //이벤트 발생 시간 |행위(권한 부여/회수) |kt직원id(타입) -> 대상자(타입) |결과
                "%s |%s |%s(%s) -> %s(%s) |%s",
                header.getTimestamp(),
                action,
                event.getOperator().getName(),
                event.getOperator().getType(),
                event.getSecurity().getTarget(),
                event.getSecurity().getTargetType(),
                event.getEvent().getResMessage()
        );
    }

    private String buildGeneralLogMessage(HeaderDto header, GeneralLogDto event) {
        return String.format(
                //이벤트 발생 시간 |level |이벤트 발생 서비스명 |host |식별자(key) - msg
                "%s |%s |%s |%s |%s - %s",
                header.getTimestamp(),
                event.getLevel(),
                header.getSource().getService(),
                header.getSource().getHost(),
                event.getKey(),
                event.getMsg()
        );
    }

    // 라우팅 키에 따라 권한 부여/회수 액션 결정
    private String determineByRoutingKey(String routingKey) {
        return routingKey.equals("operator.privacy.created") ? "권한 부여" : "권한 회수";
    }

    //error 로그
    public String buildErrorMessage(HeaderDto header, String messageBody, Exception e) {
        return String.format(
                //이벤트 발생 시간 |ERROR |이벤트 발생 서비스명 |host |식별자(key) - msg
                "%s |ERROR |%s |%s |%s - %s",
                header.getTimestamp(),
                header.getSource() != null && header.getSource().getService() != null ? header.getSource().getService() : "UNKNOWN",
                header.getSource() != null && header.getSource().getHost() != null ? header.getSource().getHost() : "UNKNOWN",
                e.getMessage(),
                messageBody
        );
    }

}*/
