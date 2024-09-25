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
 * 이벤트 타입별 분기 처리 클래스
 */
@Service
public class EventRouter {

    @Autowired
    private LogHandler logHandler;

    public void routeEvent(LogDto<?> message, String eventType, String routingKey) {
        switch (eventType) {
            case "CUSTOMER_EVENT":
                logHandler.handleCustomerEvent(message);
                break;
            case "OPERATOR_EVENT":
                logHandler.handleOperatorEvent(message);
                break;
            case "OPERATOR_PRIVACY_EVENT":
                logHandler.handleOperatorPrivacyEvent(message, routingKey);
                break;
            case "GENERAL_LOG":
                logHandler.handleGeneralLog(message);
                break;
            default:
//                logHandler.handleGeneralLog(message);
//                break;
                throw  new IllegalArgumentException(eventType);
        }
    }

}
