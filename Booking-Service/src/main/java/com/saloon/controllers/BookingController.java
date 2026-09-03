package com.saloon.controllers;

import com.saloon.domains.BookingStatus;
import com.saloon.domains.PaymentMethod;
import com.saloon.dtos.*;
import com.saloon.mappers.BookingMapper;
import com.saloon.models.Booking;
import com.saloon.models.response.PaymentLinkResponse;
import com.saloon.services.BookingService;
import com.saloon.services.clients.PaymentFeignClient;
import com.saloon.services.clients.SaloonFeignClient;
import com.saloon.services.clients.ServiceOfferingFeignClient;
import com.saloon.services.clients.UserFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final UserFeignClient userFeignClient;
    private final SaloonFeignClient saloonFeignClient;
    private final ServiceOfferingFeignClient serviceFeignClient;
    private final PaymentFeignClient paymentFeignClient;

    @PostMapping
    public ResponseEntity<PaymentLinkResponse> createBooking(@RequestParam Long saloonId, @RequestParam PaymentMethod paymentMethod, @RequestBody BookingRequestDto bookingRequest, @RequestHeader("Authorization") String jwt) {
        UserDto userDto = this.userFeignClient.getUserProfile(jwt).getBody();

        SaloonDto saloonDto = this.saloonFeignClient.getSaloonById(saloonId).getBody();
        Set<ServiceDto> serviceDtoSet = serviceFeignClient.getServicesByIds(bookingRequest.getServiceIds()).getBody();
        if(serviceDtoSet == null || serviceDtoSet.isEmpty()){
            throw new RuntimeException("Service not found.");
        }
        Booking booking = bookingService.createBooking(bookingRequest, userDto, saloonDto, serviceDtoSet);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        PaymentLinkResponse paymentResponse = paymentFeignClient.createPaymentLink(bookingDto, paymentMethod, jwt).getBody();
        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }

    @GetMapping("/customer")
    public ResponseEntity<Set<BookingDto>> getBookingByCustomerId(@RequestHeader("Authorization") String jwt) {
        UserDto userDto = userFeignClient.getUserProfile(jwt).getBody();
        if(userDto == null || userDto.getId()==null){
            throw new RuntimeException("User not found from jwt.");
        }
        List<Booking> bookings = bookingService.getBookingByCustomerId(userDto.getId());
        return ResponseEntity.ok(getBookingDtos(bookings));
    }

    @GetMapping("/saloon")
    public ResponseEntity<Set<BookingDto>> getBookingBySaloonId(@RequestHeader("Authorization") String jwt) {
        SaloonDto saloonDto = saloonFeignClient.getSaloonsByOwner(jwt).getBody();
        List<Booking> bookings = bookingService.getBookingsBySaloon(saloonDto.getId());
        return ResponseEntity.ok(getBookingDtos(bookings));
    }

    private Set<BookingDto> getBookingDtos(List<Booking> bookings){
        return bookings.stream().map(booking -> {
            UserDto userDto = new UserDto();
            userDto.setId(1L);
            return BookingMapper.toBookingDto(booking);
        }).collect(Collectors.toSet());
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long bookingId) {
        return ResponseEntity.ok(BookingMapper.toBookingDto(bookingService.getBookingById(bookingId)));
    }

    @PutMapping("/{bookingId}/{status}")
    public Booking updateBookingStatus(@PathVariable Long bookingId, @RequestParam BookingStatus status) {
        return bookingService.updateBookingStatus(bookingId, status);
    }

    @GetMapping("/slots/saloon/{saloonId}/date/{date}")
    public ResponseEntity<List<BookingSlotDto>> getBookedSlots(@PathVariable Long saloonId, @RequestParam(required = false) LocalDate date) {
        List<Booking> bookings = bookingService.getBookingsByDate(date,saloonId);
        List<BookingSlotDto> slotsDtos = bookings.stream()
                .map(booking -> {
                    BookingSlotDto slotDto = new BookingSlotDto();
                    slotDto.setStartTime(booking.getStartTime());
                    slotDto.setEndTime(booking.getEndTime());
                    return slotDto;

                }).toList();
        return ResponseEntity.ok(slotsDtos);
    }

    @GetMapping("/report/{saloonId}")
    public ResponseEntity<SaloonReport> getSaloonReport(@RequestHeader("Authorization") String jwt) {
        SaloonDto saloonDto = saloonFeignClient.getSaloonsByOwner(jwt).getBody();
        SaloonReport report = bookingService.getSaloonReport(saloonDto.getId());
        return ResponseEntity.ok(report);
    }
}
