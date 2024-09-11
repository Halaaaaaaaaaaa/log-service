package com.tisquare.petcare.demo.amqp.listener;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tisquare.petcare.demo.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class ListenerService {
    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(ListenerService.class);
    private final Logger generalLogger = LoggerFactory.getLogger("general-logger");
    private final Logger customerLogger = LoggerFactory.getLogger("customer-event-logger");
    private final Logger operatorLogger = LoggerFactory.getLogger("operator-event-logger");
    private final Logger roleLogger = LoggerFactory.getLogger("role-mgmt-logger");

    /*@RabbitListener(queues = "q_for_log_service", containerFactory = "rabbitListenerContainerFactory")
    public void receiveStringMessage(@Header String header, @Payload String message) {
        try {
            if (isJson(message)) {
                LogDto<?> logMessage = handleJsonMessage(message);
                generalLogger.info(String.valueOf(logMessage));
            } else {
                generalLogger.info("Received non-JSON message: {}", message);
            }
        } catch (Exception e) {
            generalLogger.info("=== Error message: {}", message, e.getMessage());
        }
    }

    private LogDto<?> handleJsonMessage(String messageBody) throws Exception {
        // 라우팅 키에 따라 적절한 DTO로 변환
        MessageProperties properties = new MessageProperties();
        String routingKey = properties.getReceivedRoutingKey();
        return deserializeMessageByRoutingKey(routingKey, messageBody);
    }*/

    @RabbitListener(queues = "q_for_log_service", containerFactory = "rabbitListenerContainerFactory")
    public void receiveMessage(Message amqpMessage) {
        MessageProperties properties = amqpMessage.getMessageProperties();
        String routingKey = properties.getReceivedRoutingKey();
        String messageBody = new String(amqpMessage.getBody());

        try {
            if (isJson(messageBody)) {
                // 라우팅 키에 따라 적절한 DTO 타입을 지정
                LogDto<?> message = deserializeMessageByRoutingKey(routingKey, messageBody);

                // 필드 검증
                if (message.getHeader() == null || message.getData() == null) {
                    throw new IllegalArgumentException("Message header or body is NULL");
                }

                logger.debug("Header: {}, Data: {}", message.getHeader(), message.getData());

                // 라우팅 키에 따라 처리
                handleMessageByRoutingKey(message, routingKey);

            } else {
                throw new IllegalArgumentException("Received non-JSON message");
            }
        } catch (JsonParseException | IllegalArgumentException e) {
            generalLogger.error("Invalid message received: {}", messageBody, e.getMessage());

        } catch (Exception e) {
            generalLogger.error("Error processing message: {}", messageBody, e.getMessage());

            e.printStackTrace();
        }
    }

    private LogDto<?> deserializeMessageByRoutingKey(String routingKey, String messageBody) throws Exception {
        if (routingKey.startsWith("customer")) {
            return objectMapper.readValue(messageBody, new TypeReference<LogDto<CustomerEventDto>>() {});
        } else if (routingKey.startsWith("operator")) {
            return objectMapper.readValue(messageBody, new TypeReference<LogDto<OperatorEventDto>>() {});
        } else if (routingKey.equals("log.application.created")) {
            return objectMapper.readValue(messageBody, new TypeReference<LogDto<GeneralLogDto>>() {});
        } else {
            throw new IllegalArgumentException("Unknown routing key: " + routingKey);
        }
    }

    private void handleMessageByRoutingKey(LogDto<?> message, String routingKey) {
        HeaderDto header = message.getHeader();
        String eventType = getEventTypeFromRoutingKey(routingKey);

        logger.debug("EventType: {}, DataType: {}", eventType, message.getData().getClass());

        // 운영자 이벤트와 권한 관리 이력을 다르게 처리
        if (eventType.equals("OPERATOR_PRIVACY_EVENT")) {
            String roleMgmtLogMessage = buildLogMessage(header, eventType, message.getData(), routingKey, "ROLE");
            roleLogger.info(roleMgmtLogMessage);

            String operatorLogMessage = buildLogMessage(header, eventType, message.getData(), routingKey, "OPERATOR");
            operatorLogger.info(operatorLogMessage);
        } else {
            // 나머지 이벤트 처리
            String logMessage = buildLogMessage(header, eventType, message.getData(), routingKey, eventType);

            switch (eventType) {
                case "CUSTOMER_EVENT":
                    customerLogger.info(logMessage);
                    break;
                case "OPERATOR_EVENT":
                    operatorLogger.info(logMessage);
                    break;
                case "GENERAL_LOG":
                    generalLogger.info(logMessage);
                    break;
                default:
                    generalLogger.warn("Unknown event type: {}", eventType);
                    generalLogger.info(logMessage);
                    break;
            }
        }
    }

    private String buildLogMessage(HeaderDto header, String eventType, Object message, String routingKey, String logType) {
        StringBuilder logMessageBuilder = new StringBuilder();

        if (messageContainsSpecialChars(message.toString())) {
            logMessageBuilder.append("Raw message: ").append(message.toString());
            return logMessageBuilder.toString();
        }

        if (eventType.equals("CUSTOMER_EVENT")) {
            CustomerEventDto customerEventDto = (CustomerEventDto) message;
            logMessageBuilder.append(String.format(
                    //timestamp |event.status |customer.id_uid |event.name_routingkey |customData |resMessage
                    "%s |%s |%s |%s |%s |%s",
                    header.getTimestamp(),
                    customerEventDto.getEvent().getStatus(),
                    customerEventDto.getCustomer().getId(),
                    routingKey,
                    customerEventDto.getEvent().getCustomData(),
                    customerEventDto.getEvent().getResMessage()
            ));

        } else if (eventType.equals("OPERATOR_EVENT") || eventType.equals("OPERATOR_PRIVACY_EVENT")) {
            if (message instanceof OperatorEventDto) {
                OperatorEventDto operatorEventDto = (OperatorEventDto) message;

                // 권한 관리 이력 포맷
                if ("ROLE".equals(logType)) {
                    String action = routingKey.equals("operator.privacy.created") ? "권한 부여" : "권한 회수";
                    logMessageBuilder.append(String.format(
                            "%s |%s |%s |%s(%s) -> %s(%s) |%s",
                            header.getTimestamp(),
                            operatorEventDto.getEvent().getStatus(),
                            action,
                            operatorEventDto.getOperator().getName(),
                            operatorEventDto.getOperator().getType(),
                            operatorEventDto.getSecurity().getTarget(),
                            operatorEventDto.getSecurity().getTargetType(),
                            operatorEventDto.getEvent().getResMessage()
                    ));
                } else if ("OPERATOR".equals(logType)) {
                    // 일반 운영자 이벤트 포맷
                    logMessageBuilder.append(String.format(
                            "%s |%s |%s(%s) |%s |%s |%s(%s) |%s",
                            header.getTimestamp(),
                            operatorEventDto.getEvent().getStatus(),
                            operatorEventDto.getOperator().getName(),
                            operatorEventDto.getOperator().getType(),
                            routingKey,
                            operatorEventDto.getEvent().getCustomData(),
                            operatorEventDto.getSecurity().getTarget(),
                            operatorEventDto.getSecurity().getTargetType(),
                            operatorEventDto.getEvent().getResMessage()
                    ));
                }
            }
        } else if (eventType.equals("GENERAL_LOG")) {
            if (message instanceof GeneralLogDto) {
                GeneralLogDto generalLogDto = (GeneralLogDto) message;
                logMessageBuilder.append(String.format(
                        //[timestamp] [level] [service] [host] [key] – msg
                        "[%s] [%s] [%s] [%s] [%s] - %s",
                        header.getTimestamp(),
                        generalLogDto.getLevel(),
                        header.getSource().getService(),
                        header.getSource().getHost(),
                        generalLogDto.getKey(),
                        generalLogDto.getMsg()
                ));
            }
        } else {
            logMessageBuilder.append(message.toString());
        }

        return logMessageBuilder.toString();
    }

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

    private boolean isJson(String messageBody) {
        try {
            objectMapper.readTree(messageBody);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean messageContainsSpecialChars(String message) {
        // 특수문자(\n, \t 등) 포함 여부 확인
        return message.contains("\n") || message.contains("\t") || message.contains("\\");
    }
}
