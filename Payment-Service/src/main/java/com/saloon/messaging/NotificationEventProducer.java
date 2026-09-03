package com.saloon.messaging;

import com.saloon.models.PaymentOrder;
import com.saloon.payloads.dtos.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendNotificationEvent(Long bookingId, Long userId, Long saloonId) {
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setBookingId(bookingId);
        notificationDto.setUserId(userId);
        notificationDto.setSaloonId(saloonId);
        notificationDto.setDescription("New booking got confirmed.");
        notificationDto.setType("BOOKING");
        rabbitTemplate.convertAndSend("notification-queue", notificationDto);
    }
}
