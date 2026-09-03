package com.saloon.mappers;

import com.saloon.dtos.BookingDto;
import com.saloon.models.Booking;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setCustomerId(booking.getCustomerId());
        bookingDto.setSaloonId(booking.getSaloonId());
        bookingDto.setStartTime(booking.getStartTime());
        bookingDto.setEndTime(booking.getEndTime());
        bookingDto.setServicesIds(booking.getServicesIds());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setTotalPrice(booking.getTotalPrice());
        return bookingDto;
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setCustomerId(bookingDto.getCustomerId());
        booking.setSaloonId(bookingDto.getSaloonId());
        booking.setStartTime(bookingDto.getStartTime());
        booking.setEndTime(bookingDto.getEndTime());
        booking.setServicesIds(bookingDto.getServicesIds());
        booking.setStatus(bookingDto.getStatus());
        booking.setTotalPrice(bookingDto.getTotalPrice());
        return booking;
    }
}
