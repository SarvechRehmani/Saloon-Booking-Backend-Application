package com.saloon.messaing;

import com.saloon.models.Notification;
import com.saloon.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "notification-queue")
    public void sendNotificationEventConsumer(Notification notification){
        notificationService.createNotification(notification);
    }

}
