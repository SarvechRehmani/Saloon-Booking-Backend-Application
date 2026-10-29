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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
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
        UserDto userDto = getCurrentUser(jwt);
        SaloonDto saloonDto = this.saloonFeignClient.getSaloonById(saloonId).getBody();
        if (saloonDto == null || saloonDto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Saloon not found");
        }

        Set<Long> requestedServiceIds = bookingRequest == null ? null : bookingRequest.getServiceIds();
        if (requestedServiceIds == null || requestedServiceIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one service is required");
        }
        Set<ServiceDto> serviceDtoSet = serviceFeignClient.getServicesByIds(bookingRequest.getServiceIds()).getBody();
        if (serviceDtoSet == null || serviceDtoSet.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requested services not found");
        }
        Set<Long> returnedServiceIds = serviceDtoSet.stream().map(ServiceDto::getId).collect(Collectors.toSet());
        if (returnedServiceIds.size() != requestedServiceIds.size() || !returnedServiceIds.equals(requestedServiceIds)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All requested services must exist");
        }
        if (serviceDtoSet.stream().anyMatch(service -> !saloonId.equals(service.getSaloonId()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Every selected service must belong to the requested saloon");
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
    public ResponseEntity<BookingDto> getBookingById(
            @PathVariable Long bookingId,
            @RequestHeader("Authorization") String jwt) {
        Booking booking = bookingService.getBookingById(bookingId);
        assertCanAccessBooking(getCurrentUser(jwt), booking);
        return ResponseEntity.ok(BookingMapper.toBookingDto(booking));
    }

    @PutMapping("/{bookingId}/{status}")
    public Booking updateBookingStatus(
            @PathVariable Long bookingId,
            @PathVariable BookingStatus status,
            @RequestHeader("Authorization") String jwt) {
        UserDto currentUser = getCurrentUser(jwt);
        if (status != BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only cancellation is allowed here; payment confirms bookings");
        }
        Booking booking = bookingService.getBookingById(bookingId);
        assertCanAccessBooking(currentUser, booking);
        return bookingService.updateBookingStatus(bookingId, status);
    }

    private UserDto getCurrentUser(String jwt) {
        UserDto user = userFeignClient.getUserProfile(jwt).getBody();
        if (user == null || user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be resolved from the access token");
        }
        return user;
    }

    private void assertCanAccessBooking(UserDto user, Booking booking) {
        if ("ADMIN".equalsIgnoreCase(user.getRole()) || user.getId().equals(booking.getCustomerId())) {
            return;
        }

        SaloonDto saloon = saloonFeignClient.getSaloonById(booking.getSaloonId()).getBody();
        if (saloon != null && user.getId().equals(saloon.getOwnerId())) {
            return;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this booking");
    }

    @GetMapping("/slots/saloon/{saloonId}/date/{date}")
    public ResponseEntity<List<BookingSlotDto>> getBookedSlotsByDate(
            @PathVariable Long saloonId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(toBookingSlotDtos(bookingService.getBookingsByDate(date, saloonId)));
    }

    @GetMapping("/slots/saloon/{saloonId}")
    public ResponseEntity<List<BookingSlotDto>> getBookedSlots(
            @PathVariable Long saloonId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(toBookingSlotDtos(bookingService.getBookingsByDate(date, saloonId)));
    }

    private List<BookingSlotDto> toBookingSlotDtos(List<Booking> bookings) {
        return bookings.stream()
                .map(booking -> {
                    BookingSlotDto slotDto = new BookingSlotDto();
                    slotDto.setStartTime(booking.getStartTime());
                    slotDto.setEndTime(booking.getEndTime());
                    return slotDto;
                })
                .toList();
    }

    @GetMapping("/report/{saloonId}")
    public ResponseEntity<SaloonReport> getSaloonReport(@RequestHeader("Authorization") String jwt) {
        SaloonDto saloonDto = saloonFeignClient.getSaloonsByOwner(jwt).getBody();
        SaloonReport report = bookingService.getSaloonReport(saloonDto.getId());
        return ResponseEntity.ok(report);
    }
}
