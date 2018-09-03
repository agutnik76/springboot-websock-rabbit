package com.rabbit.gut.controller;

import com.rabbit.gut.config.StompConfig;
import com.rabbit.gut.message.NotificationCreatedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Optional;

@Controller
public class MessagesController {

    @Autowired
    SimpMessagingTemplate template;
    @MessageMapping("/message/{liveid}")
    @SendTo("/topic/notifications")
    //@SendToUser("/queue/stam")
    public String sendMessage(@DestinationVariable("liveid") Long liveid,
                              @Payload NotificationCreatedMessage chatMessage,
                              Principal principal,
                              SimpMessageHeaderAccessor headerAccessor) {
        template.convertAndSendToUser(liveid.toString(), "/topic/stam",
                chatMessage);
        SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        ha.setSessionId(headerAccessor.getSessionId());
        ha.setLeaveMutable(true);
        template.convertAndSendToUser(headerAccessor.getSessionId(), "/topic/stam",
                chatMessage,ha.getMessageHeaders());

        return chatMessage.getAuthor();
    }

    @MessageMapping("/messageuser/{liveid}")
    @SendToUser("user/queue/notifications")
    public String sendMessageUser(@DestinationVariable("liveid") Long liveid,
                              @Payload NotificationCreatedMessage chatMessage,
                              //Principal principal,
                              SimpMessageHeaderAccessor headerAccessor) {
//        template.convertAndSendToUser("", "/topic" + StompConfig.NOTIFICATIONS_BROKER_ADDRESS,
//                chatMessage);

        return chatMessage.getAuthor();
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public NotificationCreatedMessage addUser(@Payload NotificationCreatedMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getAuthor());
        return chatMessage;
    }
}
