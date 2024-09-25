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
 * 라우팅 키 별 DTO 변환 및 검증 처리 클래스
 */
@Service
public class MessageDeserializer {

    @Autowired
    private ObjectMapper objectMapper;

    public LogDto<?> deserializeByRoutingKey(String routingKey, String messageBody) throws Exception {
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
}
