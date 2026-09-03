package com.saloon.mapper;

import com.saloon.models.Notification;
import com.saloon.payloads.dtos.BookingDto;
import com.saloon.payloads.dtos.NotificationDto;

public class NotificationMapper {

    public static NotificationDto toNotificationDto(Notification notification, BookingDto bookingDto){
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setDescription(notification.getDescription());
        dto.setIsRead(notification.getIsRead());
        dto.setUserId(notification.getUserId());
        dto.setBookingId(notification.getBookingId());
        dto.setBooking(bookingDto);
        dto.setSaloonId(notification.getSaloonId());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    public static Notification toNotification(NotificationDto dto){
        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setType(dto.getType());
        notification.setDescription(dto.getDescription());
        notification.setIsRead(dto.getIsRead());
        notification.setUserId(dto.getUserId());
        notification.setBookingId(dto.getBookingId());
        notification.setSaloonId(dto.getSaloonId());
        notification.setCreatedAt(dto.getCreatedAt());
        return notification;

    }
}
