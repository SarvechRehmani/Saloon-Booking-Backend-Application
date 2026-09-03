package com.saloon.service;

import com.saloon.mapper.NotificationMapper;
import com.saloon.models.Notification;
import com.saloon.payloads.dtos.BookingDto;
import com.saloon.payloads.dtos.NotificationDto;
import com.saloon.repositories.NotificationRepository;
import com.saloon.service.clients.BookingFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepository notificationRepository;
    private final BookingFeignClient bookingFeignClient;

    @Override
    public NotificationDto createNotification(Notification notification) {
        Notification savedNotification = this.notificationRepository.save(notification);
        BookingDto bookingDto = this.bookingFeignClient.getBookingById(savedNotification.getBookingId()).getBody();
        return NotificationMapper.toNotificationDto(savedNotification,bookingDto);
    }

    @Override
    public List<Notification> getAllNotificationsByUserId(Long userId) {
        return this.notificationRepository.findByUserId(userId);
    }

    @Override
    public List<Notification> getAllNotificationsBySaloonId(Long saloonId) {
        return this.notificationRepository.findBySaloonId(saloonId);
    }

    @Override
    public Notification markNotificationAsRead(Long notificationId) {
        return this.notificationRepository.findById(notificationId)
                .map(notification -> {
                    notification.setIsRead(true);
                    return this.notificationRepository.save(notification);
                }).orElseThrow(()-> new RuntimeException("Notification not found"));
    }
}
