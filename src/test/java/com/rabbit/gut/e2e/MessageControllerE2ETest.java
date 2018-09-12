package com.rabbit.gut.e2e;

import com.rabbit.gut.Application;
import com.rabbit.gut.config.AuthEveryoneUserAuthServiceImpl;
import com.rabbit.gut.message.NotificationCreatedMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(JUnitPlatform.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
@Import(AuthEveryoneUserAuthServiceImpl.class)
//@WithMockUser(authorities = "111")
//TODO not working with security enabled
@WithUserDetails(userDetailsServiceBeanName = "secdetails", value = "111")
public class MessageControllerE2ETest {
    @Value("${local.server.port}")
    private int port;
    private String URL;

    private static final String SEND_ENDPOINT = "/message";
    private static final String SUBSCRIBE_ENDPOINT = "/topic/notifications";

    private CompletableFuture<NotificationCreatedMessage> completableFuture;

    @BeforeEach
    public void setup() throws Exception {
        completableFuture = new CompletableFuture<NotificationCreatedMessage>();
        URL = "ws://111@localhost:" + port + "/socket/sockJs";

    }

    @Test
    public void testCreateEndpoint() throws Exception {
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, SECONDS);

        stompSession.subscribe(SUBSCRIBE_ENDPOINT, new DefaultStompFrameHandler());
        stompSession.send(SEND_ENDPOINT, NotificationCreatedMessage.builder()
                .author("test")
                .employeeId(111L)
                .build());

        NotificationCreatedMessage gameState = completableFuture.get(10, SECONDS);

        assertNotNull(gameState);
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class DefaultStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return NotificationCreatedMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture.complete((NotificationCreatedMessage) o);
        }
    }
}
