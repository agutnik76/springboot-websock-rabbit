package com.rabbit.gut.config;

import com.rabbit.gut.service.WebSocketHandShakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * topic for broadcast messages
     */
    public static final String NOTIFICATIONS_ALL_ADDRESS = "/topic/notifications";
    /**
     * topic for specific user
     */
    public static final String NOTIFICATIONS_TO_USER = "/topic/personal";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/socket/sockJs/")
                .addInterceptors(new WebSocketHandShakeInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
        registry.enableStompBrokerRelay(NOTIFICATIONS_ALL_ADDRESS, NOTIFICATIONS_TO_USER)
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
    }
}
