package com.tisquare.petcare.log.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean
    public Queue logServiceQueue() {
        return new Queue("q_for_log_service");
    }

    @Bean
    public TopicExchange logsExchange() {
        return new TopicExchange("amq.topic");
    }

    @Bean
    public Binding businessLogBinding(Queue logServiceQueue, TopicExchange logsExchange) {
        return BindingBuilder.bind(logServiceQueue).to(logsExchange).with("log.application.created");
    }

    @Bean
    public Binding customerEventLogBinding(Queue logServiceQueue, TopicExchange logsExchange) {
        return BindingBuilder.bind(logServiceQueue).to(logsExchange).with("customer.*.*");
    }

    @Bean
    public Binding operatorEventLogBinding(Queue logServiceQueue, TopicExchange logsExchange) {
        return BindingBuilder.bind(logServiceQueue).to(logsExchange).with("operator.*.*");
    }

    @Bean
    public Binding operatorPrivateCreateBinding(Queue logServiceQueue, TopicExchange logsExchange) {
        return BindingBuilder.bind(logServiceQueue).to(logsExchange).with("operator.privacy.created");
    }

    @Bean
    public Binding operatorPrivateDeleteBinding(Queue logServiceQueue, TopicExchange logsExchange) {
        return BindingBuilder.bind(logServiceQueue).to(logsExchange).with("operator.privacy.delete");
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    //RabbitTemplate 설정
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    //SimpleMessageListenerContainer 설정
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPrefetchCount(10); // pre-fetch count 설정

        return factory;
    }

    //Client ID 설정
    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(host, port);
            factory.setUsername(username);
            factory.setPassword(password);

        //난수 UUID 4글자만
        //factory.setConnectionNameStrategy(connectionFactory -> "log_service-" + UUID.randomUUID().toString().substring(0, 4));
        //난수 숫자 랜덤 4자리 수
        factory.setConnectionNameStrategy(connectionFactory -> "log_service-" + (new Random().nextInt(9000) + 1000));

        return factory;
    }

}
