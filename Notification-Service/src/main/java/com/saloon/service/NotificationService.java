package com.saloon.service;

import com.saloon.models.Notification;
import com.saloon.payloads.dtos.NotificationDto;

import java.util.List;

public interface NotificationService {

    NotificationDto createNotification(Notification notification);
    List<Notification> getAllNotificationsByUserId(Long userId);
    List<Notification> getAllNotificationsBySaloonId(Long saloonId);
     Notification markNotificationAsRead(Long notificationId);
}
