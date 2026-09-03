package com.saloon.services.impl;

import com.saloon.domains.BookingStatus;
import com.saloon.dtos.*;
import com.saloon.models.Booking;
import com.saloon.models.PaymentOrder;
import com.saloon.repositores.BookingRepository;
import com.saloon.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public Booking createBooking(BookingRequestDto booking, UserDto userDto, SaloonDto saloonDto, Set<ServiceDto> serviceDtoSet) {
        int totalDuration = serviceDtoSet.stream().mapToInt(ServiceDto::getDuration).sum();
        LocalDateTime bookingStartTime = booking.getStartTime();
        LocalDateTime bookingEndTime = bookingStartTime.plusMinutes(totalDuration);
        Boolean isSlotAvailable = isTimeSlotAvailable(saloonDto, bookingStartTime, bookingEndTime);
        double totalPrice = serviceDtoSet.stream().mapToDouble(ServiceDto::getPrice).sum();
        Set<Long> servicesIdList = serviceDtoSet.stream().map(ServiceDto::getId).collect(Collectors.toSet());
        Booking newBooking = new Booking();
        newBooking.setCustomerId(userDto.getId());
        newBooking.setSaloonId(saloonDto.getId());
        newBooking.setServicesIds(servicesIdList);
        newBooking.setStatus(BookingStatus.PENDING);
        newBooking.setStartTime(bookingStartTime);
        newBooking.setEndTime(bookingEndTime);
        newBooking.setTotalPrice(totalPrice);
        return bookingRepository.save(newBooking);
    }

    public Boolean isTimeSlotAvailable(SaloonDto saloonDto,LocalDateTime bookingStartTime, LocalDateTime bookingEndTime) {
        List<Booking> existingBookings = getBookingsBySaloon(saloonDto.getId());
        LocalDateTime saloonOpenTime = saloonDto.getOpenTime().atDate(bookingStartTime.toLocalDate());
        LocalDateTime saloonCloseTime = saloonDto.getCloseTime().atDate(bookingStartTime.toLocalDate());
        if(bookingStartTime.isBefore(saloonOpenTime) || bookingEndTime.isAfter(saloonCloseTime)){
            throw new RuntimeException("Booking time must be within saloons working hour");
        }

        for (Booking existingBooking : existingBookings) {
            LocalDateTime existingBookingStartTime = existingBooking.getStartTime();
            LocalDateTime existingBookingEndTime = existingBooking.getEndTime();
            if(bookingStartTime.isBefore(existingBookingEndTime) && bookingEndTime.isAfter(existingBookingStartTime)){
                throw new RuntimeException("Slot not available, Choose different time.");
            }
            if(bookingStartTime.isEqual(existingBookingStartTime) || bookingEndTime.isEqual(existingBookingEndTime)){
                throw new RuntimeException("Slot not available, Choose different time.");
            }
        }
        return true;
    }

    @Override
    public List<Booking> getBookingByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Booking> getBookingsBySaloon(Long saloonId) {
        return bookingRepository.findBySaloonId(saloonId);
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id : " + id));
    }

    @Override
    public Booking updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = getBookingById(bookingId);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsByDate(LocalDate date, Long saloonId) {
        List<Booking> allBookings = getBookingsBySaloon(saloonId);
        if(date == null){
            return allBookings;
        }
        return allBookings.stream()
                .filter(booking -> isSameDate(booking.getStartTime(), date) || isSameDate(booking.getEndTime(), date))
                .toList();
    }

    private boolean isSameDate(LocalDateTime dateTime, LocalDate date) {
        return dateTime.toLocalDate().isEqual(date);
    }

    @Override
    public SaloonReport getSaloonReport(Long saloonId) {
        List<Booking> bookings = getBookingsBySaloon(saloonId);
        Double totalEarning = bookings.stream().mapToDouble(Booking::getTotalPrice).sum();
        Integer totalBooking = bookings.size();
        List<Booking> cancelledBooking = bookings.stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.CANCELLED))
                .toList();
        Double totalRefund = cancelledBooking.stream().mapToDouble(Booking::getTotalPrice).sum();
        SaloonReport report = new SaloonReport();
        report.setSaloonId(saloonId);
        report.setTotalBookings(totalBooking);
        report.setCancelBookings(cancelledBooking.size());
        report.setTotalEarning(totalEarning);
        report.setTotalRefund(totalRefund);
        return report;
    }

    @Override
    public Booking bookingSuccess(PaymentOrder paymentOrder) {
        Booking existingBooking = getBookingById(paymentOrder.getBookingId());
        existingBooking.setStatus(BookingStatus.CONFIRMED);
        return this.bookingRepository.save(existingBooking);
    }
}
