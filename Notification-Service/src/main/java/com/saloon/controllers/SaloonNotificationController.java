package com.saloon.controllers;

import com.saloon.mapper.NotificationMapper;
import com.saloon.models.Notification;
import com.saloon.payloads.dtos.BookingDto;
import com.saloon.payloads.dtos.NotificationDto;
import com.saloon.service.NotificationService;
import com.saloon.service.clients.BookingFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications/saloon-owner")
@RequiredArgsConstructor
public class SaloonNotificationController {

    private final NotificationService notificationService;
    private final BookingFeignClient bookingFeignClient;

    @GetMapping("/saloon/{saloonId}")
    public ResponseEntity<List<NotificationDto>> getAllNotificationsBySaloonId(@PathVariable Long saloonId) {
        return ResponseEntity.ok(notificationService.getAllNotificationsBySaloonId(saloonId).stream()
                .map(notification -> {
                    BookingDto bookingDto = bookingFeignClient.getBookingById(notification.getBookingId()).getBody();
                    return NotificationMapper.toNotificationDto(notification,bookingDto);
                })
                .toList());
    }

   @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDto> markNotificationAsRead(@PathVariable Long notificationId) {
       Notification notification = notificationService.markNotificationAsRead(notificationId);
       BookingDto bookingDto = bookingFeignClient.getBookingById(notification.getBookingId()).getBody();
        return ResponseEntity.ok(NotificationMapper.toNotificationDto(notification,bookingDto));
    }

}
