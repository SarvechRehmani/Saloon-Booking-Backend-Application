package com.saloon.services;

import com.saloon.domains.BookingStatus;
import com.saloon.dtos.*;
import com.saloon.models.Booking;
import com.saloon.models.PaymentOrder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface BookingService {

    Booking createBooking(BookingRequestDto booking, UserDto userDto, SaloonDto saloonDto, Set<ServiceDto> serviceDtoSet);
//    Booking updateBooking(BookingRequestDto booking, UserDto userDto, SaloonDto saloonDto, Set<ServiceDto> serviceDtoSet);
    List<Booking> getBookingByCustomerId(Long customerId);
    List<Booking> getBookingsBySaloon(Long saloonId);
    Booking getBookingById(Long id);
    Booking updateBookingStatus(Long bookingId, BookingStatus status);
    List<Booking> getBookingsByDate(LocalDate date, Long saloonId);
    SaloonReport getSaloonReport(Long saloonId);

    Booking bookingSuccess(PaymentOrder paymentOrder);
}
