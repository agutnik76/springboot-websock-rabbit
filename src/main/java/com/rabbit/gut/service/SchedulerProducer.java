package com.rabbit.gut.service;

import com.rabbit.gut.message.NotificationCreatedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SchedulerProducer {

    @Autowired
    private volatile RabbitTemplate rabbitTemplate;

    private final AtomicInteger counter = new AtomicInteger();
    public static final String NOTIFICATIONS_QUEUE_NAME = "notifications";

    @Scheduled(fixedRate = 300000)
    public void sendMessage() {
        NotificationCreatedMessage m = NotificationCreatedMessage
                .builder()
                .author(counter.incrementAndGet() + "")
                .employeeId(1L)
                .build();
        //rabbitTemplate.convertAndSend("", NOTIFICATIONS_QUEUE_NAME, m);
    }

}
