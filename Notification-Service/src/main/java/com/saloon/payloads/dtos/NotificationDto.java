package com.saloon.payloads.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDto {
    private Long id;
    private String description;
    private String type;
    private Boolean isRead = false;
    private Long userId;
    private Long bookingId;
    private Long saloonId;
    private LocalDateTime createdAt;
    private BookingDto booking;
}
