package com.rabbit.gut.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    public static final String NOTIFICATIONS_BROKER_ADDRESS = "/notifications";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/socket/sockJs/")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
                //.enableSimpleBroker(NOTIFICATIONS_BROKER_ADDRESS);
                //.enableStompBrokerRelay(NOTIFICATIONS_BROKER_ADDRESS);
                //.setHeartbeatValue(new long[]{heartbeatIncoming, heartbeatOutcoming})
                //.setTaskScheduler(new DefaultManagedTaskScheduler());
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost("localhost")
                .setRelayPort(61613)// 61613 https://stackoverflow.com/questions/26451367/configuring-rabbitmq-with-spring4-stomp-and-socksjs-application?noredirect=1&lq=1
                .setClientLogin("guest")
                .setClientPasscode("guest");
    }
}