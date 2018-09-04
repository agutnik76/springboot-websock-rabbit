package com.rabbit.gut.controller;

import com.rabbit.gut.config.StompConfig;
import com.rabbit.gut.message.NotificationCreatedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessagesController {

    SimpMessagingTemplate template;

    @Autowired
    public MessagesController() {

    }

    /**
     * Broadcast message to all connected websockets
     */
    @MessageMapping("/message/{liveid}")
    @SendTo("/topic/notifications")
    public String sendMessage(@DestinationVariable("liveid") Long liveid,
                              @Payload NotificationCreatedMessage chatMessage,
                              Principal principal,
                              SimpMessageHeaderAccessor headerAccessor) {

        return chatMessage.getAuthor();
    }

    /**
     * Sends message to specific user
     */
    @MessageMapping("/messageuser/{liveid}")
    public void sendMessageUser(@DestinationVariable("liveid") Long liveid,
                                @Payload NotificationCreatedMessage chatMessage,
                                Principal principal,
                                SimpMessageHeaderAccessor headerAccessor) {
        //send by user id
        template.convertAndSendToUser(liveid.toString(), StompConfig.NOTIFICATIONS_TO_USER,
                chatMessage);
        //send by sessionid
//        SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor
//                .create(SimpMessageType.MESSAGE);
//        ha.setSessionId(headerAccessor.getSessionId());
//        ha.setLeaveMutable(true);
//        template.convertAndSendToUser(headerAccessor.getSessionId(), StompConfig.NOTIFICATIONS_TO_USER,
//                chatMessage,ha.getMessageHeaders());
    }
}
