package com.rabbit.gut.controller;

import com.rabbit.gut.message.NotificationCreatedMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class MessagesController {

    @MessageMapping("/message")
    @SendTo("/topic/notifications")
    public String sendMessage(@Payload NotificationCreatedMessage chatMessage) {
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
