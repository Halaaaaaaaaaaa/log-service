package com.tisquare.petcare.log.amqp.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.tisquare.petcare.log.dto.HeaderDto;
import com.tisquare.petcare.log.dto.LogDto;
import com.tisquare.petcare.log.validation.ValidatorGroup.HeaderGroup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;


/**
 * 메시지 수신 처리 클래스
 */
@Service
public class ListenerService {

    @Autowired
    private MessageDeserializer messageDeserializer;
    @Autowired
    private EventRouter eventRouter;
    @Autowired
    private Validator validator;

    private final Logger generalLogger = LoggerFactory.getLogger("general-logger");

    @RabbitListener(queues = "q_for_log_service", containerFactory = "rabbitListenerContainerFactory")
    public void receiveMessage(Message amqpMessage) {
        String routingKey = amqpMessage.getMessageProperties().getReceivedRoutingKey();
        String messageBody = new String(amqpMessage.getBody());

        try {
            //라우팅 키에 따라 메시지 역직렬화
            LogDto<?> message = messageDeserializer.deserializeByRoutingKey(routingKey, messageBody);
            validateHeader(message.getHeader());

            String eventType = getEventTypeFromRoutingKey(routingKey);
            eventRouter.routeEvent(message, eventType, routingKey);
        } catch (JsonParseException | IllegalArgumentException e) {
            String errorLog = formatErrorLogMessage(messageBody, e);
            generalLogger.error(errorLog);
//            generalLogger.error("Invalid message received: {}", messageBody, e.getMessage());
        } catch (Exception e) {
            String errorLog = formatErrorLogMessage(messageBody, e);
            generalLogger.error(errorLog);
//            generalLogger.error("Error processing message: {}", messageBody, e.getMessage());
        }
    }

    //라우팅 키 기반으로 이벤트 타입 결정
    private String getEventTypeFromRoutingKey(String routingKey) {
        if (routingKey.equals("operator.privacy.created") || routingKey.equals("operator.privacy.deleted")) {
            return "OPERATOR_PRIVACY_EVENT";
        } else if (routingKey.startsWith("operator")) {
            return "OPERATOR_EVENT";
        } else if (routingKey.startsWith("customer")) {
            return "CUSTOMER_EVENT";
        } else if (routingKey.equals("log.application.created")) {
            return "GENERAL_LOG";
        } else {
            return "UNKNOWN_EVENT";
        }
    }

    //에러 메시지 로그로 출력하기 위한 포맷팅 메서드
    private String formatErrorLogMessage(String messageBody, Exception e) {
        try {
            JSONObject jsonObject = new JSONObject(messageBody);
            String timestamp = jsonObject.optString("timestamp", "UNKNOWN");
            JSONObject source = jsonObject.optJSONObject("source");
            String service = source != null ? source.optString("service", "UNKNOWN") : "UNKNOWN";
            String host = source != null ? source.optString("host", "UNKNOWN") : "UNKNOWN";

            //일반 로그 규격에 맞게 포맷팅
            return String.format("%s |ERROR |%s |%s - %s",
                    timestamp, service, host, e.getClass().getSimpleName() + ": " + e.getMessage());
        } catch (Exception ex) {
            //JSON 파싱 실패 시 예외 메시지 출력
            return String.format("Error message: [Invalid JSON] - %s", e.getMessage());
        }
    }

    private void validateHeader(HeaderDto header) {
        Set<ConstraintViolation<HeaderDto>> violations = validator.validate(header, HeaderGroup.class);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}
