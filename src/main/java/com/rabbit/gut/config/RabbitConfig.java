package com.rabbit.gut.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.*;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import java.util.stream.Collectors;

@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {

    @Value("${spring.rabbitmq.host}")
    private String HOST;
    @Value("${spring.rabbitmq.port}")
    private Integer PORT;
    @Value("${spring.rabbitmq.username}")
    private String USERNAME;
    @Value("${spring.rabbitmq.password}")
    private String PASSWORD;
    @Value("${spring.profiles.active:}")
    private String ACTIVE_PROFILE;
    /**
     * Notifications queue
     */
    public static final String NOTIFICATIONS_QUEUE_NAME = "notifications";

    /**
     * Users notifications queue
     */
    @Bean
    public Queue notificationsQueue() {
        return new Queue(getFullQueueName(NOTIFICATIONS_QUEUE_NAME),
                true,
                false,
                false);
    }

    /**
     * RabbitMQ connections settings
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(HOST);
        connectionFactory.setPort(PORT);
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        return connectionFactory;
    }

    /**
     * sets messageConverter to write to queue in json format
     * overrides send - just in case, for future use
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory()) {
            @Override
            public void send(String exchange, String routingKey, Message message, CorrelationData correlationData) throws AmqpException {
                super.send(exchange, routingKey, message, correlationData);
            }
        };

        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter(objectMapper());
        rabbitTemplate.setMessageConverter(messageConverter);

        return rabbitTemplate;
    }

    /**
     * objectMapper and date serialization
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    /**
     * sets messageHandler for listeners to read json
     */
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        rabbitListenerEndpointRegistrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    /**
     * creates messagehandler with json converter
     */
    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(consumerJackson2MessageConverter());
        return factory;
    }

    /**
     * json converter
     */
    private MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
        mappingJackson2MessageConverter.setObjectMapper(objectMapper());
        return mappingJackson2MessageConverter;
    }

    /**
     * add profile name if exists to endpoints names
     */
    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory() {
            @Override
            public SimpleMessageListenerContainer createListenerContainer(RabbitListenerEndpoint endpoint) {
                if (endpoint instanceof MethodRabbitListenerEndpoint) {
                    MethodRabbitListenerEndpoint methodEndpoint = (MethodRabbitListenerEndpoint) endpoint;
                    methodEndpoint.setQueueNames(
                            methodEndpoint.getQueueNames().stream()
                                    .map(RabbitConfig.this::getFullQueueName)
                                    .collect(Collectors.joining())
                    );
                }

                return super.createListenerContainer(endpoint);
            }
        };
        simpleRabbitListenerContainerFactory.setConnectionFactory(connectionFactory());
        return simpleRabbitListenerContainerFactory;
    }

    private String getFullQueueName(String name) {
        return !ACTIVE_PROFILE.isEmpty() ? name + '-' + ACTIVE_PROFILE : name;
    }
}
