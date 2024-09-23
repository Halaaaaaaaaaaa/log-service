package com.tisquare.petcare.test;

import org.junit.jupiter.api.Test;

class testLogMessage {

    @Test
    void testRoutingKeyCustom() {
        //{주체}.{도메인}.{이벤트}
        String routingKey = "customer.funeral.outbound-requested";
        String[] routingKeyParts = routingKey.split("\\.");

        String domain = routingKeyParts[1];
        String event = routingKeyParts[2];

        String timestamp = "2024-09-12 17:15:30.123";
        String status = "S00000";
        String id = "heysh";
        String customData = "{reservation_id: 예약id, translated_certificate_no: 증서번호}";

        StringBuilder logMessageBuilder = new StringBuilder();
        logMessageBuilder.append(String.format(
                "%s |%s |%s |%s %s |%s",
                timestamp,
                status,
                id,
                domain,
                event,
                customData
        ));

        System.out.println(logMessageBuilder.toString());

    }
}
