package com.tisquare.petcare.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tisquare.petcare.log.amqp.service.ListenerService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

public class testRabbitMQPublisher {

    private static final String QUEUE_NAME = "q_for_log_service";
    private static final String ROUTING_KEY = "log.application.created";
    private static final String EXCHANGE_NAME = "amq.topic";

    @Autowired
    ListenerService listenerService;

    @Test //1초에 메시지 50개 발행
    public void publishMessagesTest() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.30.40");
            factory.setPort(9672);
            factory.setUsername("log_service");
            factory.setPassword("log_service.123");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            int messageCount = 50; // 초당 전송할 메시지 수

            // 1초 동안 50개의 메시지 전송
            for (int i = 0; i < messageCount; i++) {
                // 현재 타임스탬프 생성
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                // JSON 메시지 생성
                JSONObject payload = new JSONObject();
                payload.put("timestamp", timestamp);

                JSONObject source = new JSONObject();
                source.put("service", "USER-SERVICE");
                source.put("host", "localhost");
                source.put("ip", "127.0.0.1");
                payload.put("source", source);

                JSONObject data = new JSONObject();
                data.put("key", "key11");
                data.put("level", "INFO");
                data.put("msg", "test2");
                payload.put("data", data);

                // 메시지 JSON 문자열로 변환
                String message = payload.toString();

                // 메시지 전송
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "' " + (i + 1));
            }

            // 1초 대기
            Thread.sleep(1000);

        }

    }

    @Test //1초에 메시지 50개씩 5초 동안 발행
    public void publishMessagesTest2() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.30.40");
        factory.setPort(9672);
        factory.setUsername("log_service");
        factory.setPassword("log_service.123");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            int messageCount = 50;
            int totalDurationInSeconds = 5;

            // 10초 동안 매초 50개의 메시지 전송
            for (int second = 0; second < totalDurationInSeconds; second++) {
                for (int i = 0; i < messageCount; i++) {
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                    JSONObject payload = new JSONObject();
                    payload.put("timestamp", timestamp);

                    JSONObject source = new JSONObject();
                    source.put("service", "USER-SERVICE");
                    source.put("host", "localhost");
                    source.put("ip", "127.0.0.1");
                    payload.put("source", source);

                    JSONObject data = new JSONObject();
                    data.put("key", "key" + (i + 1)); // key 값을 동적으로 설정
                    data.put("level", "INFO");
                    data.put("msg", "This is a test log message.");
                    payload.put("data", data);

                    // 로그 규격으로 변환: timestamp |level |service |host |key - msg
                    String logMessage = String.format("%s |%s |%s |%s |%s - %s",
                            payload.getString("timestamp"),
                            data.getString("level"),
                            source.getString("service"),
                            source.getString("host"),
                            data.getString("key"),
                            data.getString("msg")
                    );

                    // 메시지 JSON 문자열로 변환
                    String message = payload.toString();

                    // 메시지 전송
                    channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, logMessage.getBytes("UTF-8"));
                    System.out.println(logMessage);
                }

                // 1초 대기
                Thread.sleep(1000);
            }
        }
    }

    @Test
    public void publishMessagesTest3() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.30.40");
        factory.setPort(9672);
        factory.setUsername("log_service");
        factory.setPassword("log_service.123");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            int messageCount = 50;
            int totalDurationInSeconds = 5;

            // 5초 동안 매초 50개의 메시지 전송
            for (int second = 0; second < totalDurationInSeconds; second++) {
                for (int i = 0; i < messageCount; i++) {
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                    //timestamp |level |service |host |key - msg
                    String logMessage = String.format("%s |INFO |USER-SERVICE |localhost |key%d - This is a test log message.",
                            timestamp,
                            (i + 1)
                    );

                    // 메시지 전송 (문자열 형태로)
                    channel.basicPublish("", QUEUE_NAME, null, logMessage.getBytes("UTF-8"));
                    System.out.println(logMessage);
                }

                // 1초 대기
                Thread.sleep(1000); // 1초 대기
            }
        }
    }

    @Test
    public void publishMessagesTest4() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.30.40");
        factory.setPort(9672);
        factory.setUsername("log_service");
        factory.setPassword("log_service.123");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            int messageCount = 50;
            int totalDurationInSeconds = 5;

            // 5초 동안 매초 50개의 메시지 전송
            for (int second = 0; second < totalDurationInSeconds; second++) {
                for (int i = 0; i < messageCount; i++) {
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                    // 메시지 규격: timestamp |level |service |host |key - msg
                    String logMessage = String.format(
                            "%s |%s |%s |%s |%s - %s",
                            timestamp,
                            "INFO",
                            "USER-SERVICE",
                            "localhost",
                            "key" + (i + 1),
                            "This is a test log message."
                    );

                    //channel.basicPublish(EXCHANGE_NAME, QUEUE_NAME, null, logMessage.getBytes("UTF-8"));
                    channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, logMessage.getBytes("UTF-8"));
                    System.out.println(logMessage);
                }

                // 1초 대기
                Thread.sleep(1000); // 1초 대기
            }
        }
    }



}
