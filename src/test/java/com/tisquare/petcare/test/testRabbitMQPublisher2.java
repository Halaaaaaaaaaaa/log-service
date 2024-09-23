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
    private static final String EXCHANGE_NAME = "amq.topic";

    @Test
    public void publishMessagesTest() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitHost);
        factory.setPort(rabbitPort);
        factory.setUsername(rabbitUsername);
        factory.setPassword(rabbitPassword);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            int messageCount = 50;
            int totalDurationInSeconds = 5;

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
                    System.out.println(message);
                }

                Thread.sleep(1000);
            }
        }
    }

}
