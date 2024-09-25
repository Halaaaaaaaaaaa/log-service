package com.tisquare.petcare.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.tisquare.petcare.log.LogServiceApplication;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SpringBootTest(classes = LogServiceApplication.class)
@ActiveProfiles("40")
public class testRabbitMQPublisher2 {

    @Value("${spring.rabbitmq.host}")
    private String rabbitHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    private static final String ROUTING_KEY = "log.application.created";

    private static final String ROUTING_KEY2 = "customer.access.logged-in";

    private static final String ROUTING_KEY3 = "operator.board.deleted";

    private static final String EXCHANGE_NAME = "amq.topic";

    @Test
    public void publishMessagesGeneralLogTest() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitHost);
            factory.setPort(rabbitPort);
            factory.setUsername(rabbitUsername);
            factory.setPassword(rabbitPassword);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            int messageCount = 50;
            int totalDurationInSeconds = 2;

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
                    data.put("key", "key" + (i + 1));
                    data.put("level", "INFO");
                    data.put("msg", "This is a test log message.");
                    payload.put("data", data);

                    String message = payload.toString();

                    channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
//                    System.out.println(message);
                }

                Thread.sleep(1000);
            }
        }
    }

    @Test
    public void publishMessagesCustomerLogTest() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitHost);
            factory.setPort(rabbitPort);
            factory.setUsername(rabbitUsername);
            factory.setPassword(rabbitPassword);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            int messageCount = 50;
            int totalDurationInSeconds = 2;
            Random random = new Random();

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

                    JSONObject customer = new JSONObject();
                        customer.put("id", "customer" + random.nextInt(1000) + 1);
                        customer.put("ip", "192.168.30.0" + random.nextInt(10) + 1);
                        customer.put("agent", "CHROME");

                    JSONObject event = new JSONObject();
                        event.put("name", "로그인");
                        event.put("status", "SUCCESS");
                        event.put("resCode", "200");
                        event.put("resMessage", "성공");
                        event.put("duration", "123");

                    Map<String, Object> customData = new HashMap<>();
                        customData.put("page", "login");
                        customData.put("attempt", random.nextInt(3) + 1);
                    event.put("customData", customData);

                    data.put("customer", customer);
                    data.put("url", "/user/v1/customer/login");
                    data.put("event", event);

                    payload.put("data", data);

                    String message = payload.toString();

                    channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY2, null, message.getBytes("UTF-8"));
//                    System.out.println(message);
                }

                Thread.sleep(1000);
            }

        }

    }

    @Test
    public void publishMessagesOperatorLogTest() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(rabbitHost);
            factory.setPort(rabbitPort);
            factory.setUsername(rabbitUsername);
            factory.setPassword(rabbitPassword);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            int messageCount = 50;
            int totalDurationInSeconds = 3;
            Random random = new Random();

            for (int second = 0; second < totalDurationInSeconds; second++) {
                for (int i = 0; i < messageCount; i++) {
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

                    JSONObject payload = new JSONObject();
                    payload.put("timestamp", timestamp);

                    JSONObject source = new JSONObject();
                    source.put("service", "FUNERAL-SERVICE");
                    source.put("host", "localhost");
                    source.put("ip", "127.0.0.1");
                    payload.put("source", source);

                    JSONObject data = new JSONObject();

                    JSONObject operator = new JSONObject();
                    operator.put("id", "customer" + random.nextInt(1000) + 1);
                    operator.put("name", "홍길동");
                    operator.put("division", "KT ADMIN");
                    operator.put("ip", "192.168.30.0" + random.nextInt(10) + 1);
                    operator.put("agent", "CHROME");

                    JSONObject event = new JSONObject();
                    event.put("name", "공지사항 삭제");
                    event.put("status", "SUCCESS");
                    event.put("resCode", "200");
                    event.put("resMessage", "성공");
                    event.put("duration", "123");

                    Map<String, Object> customData = new HashMap<>();
                    customData.put("notice_id", "공지" + random.nextInt(10) + 1);
                    customData.put("title", "공지 사항" + random.nextInt(10) + 1);
                    event.put("customData", customData);

                    data.put("operator", operator);
                    data.put("url", "/funeral/v1/operator/board/delete");
                    data.put("event", event);

                    payload.put("data", data);

                    String message = payload.toString();

                    channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY3, null, message.getBytes("UTF-8"));
//                    System.out.println(message);
                }

                Thread.sleep(1000);
            }

        }

    }

}
