package com.rabbit.gut.service;

import com.rabbit.gut.config.RabbitConfig;
import com.rabbit.gut.config.StompConfig;
import com.rabbit.gut.message.NotificationCreatedMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationQueueListener {

    private final SimpMessagingTemplate template;

    @Autowired
    public NotificationQueueListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    /**
     * message handler, forwards message from queue to frontend client
     */
    @RabbitListener(queues = {RabbitConfig.NOTIFICATIONS_QUEUE_NAME})
    public void processNewNotificationEvent(@Payload NotificationCreatedMessage notification) {
        log.info("Получено сообщение из очереди {} контент: {}", RabbitConfig.NOTIFICATIONS_QUEUE_NAME, notification.toString());
        SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        ha.setSessionId(notification.getEmployeeId().toString());
        ha.setLeaveMutable(true);
        template.convertAndSendToUser(notification.getEmployeeId().toString(), StompConfig.NOTIFICATIONS_TO_USER,
                notification);
    }
}
