package com.saloon.controllers;

import com.saloon.mapper.NotificationMapper;
import com.saloon.models.Notification;
import com.saloon.payloads.dtos.BookingDto;
import com.saloon.payloads.dtos.NotificationDto;
import com.saloon.service.NotificationService;
import com.saloon.service.clients.BookingFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final BookingFeignClient bookingFeignClient;

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@RequestBody Notification notification) {
        return new ResponseEntity<>(notificationService.createNotification(notification), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDto>> getAllNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getAllNotificationsByUserId(userId).stream()
                .map(notification -> {
                    BookingDto bookingDto = bookingFeignClient.getBookingById(notification.getBookingId()).getBody();
                    return NotificationMapper.toNotificationDto(notification,bookingDto);
                })
                .toList());
    }
}
