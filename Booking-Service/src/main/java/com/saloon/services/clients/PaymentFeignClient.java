package com.saloon.services.clients;

import com.saloon.domains.PaymentMethod;
import com.saloon.dtos.BookingDto;
import com.saloon.models.response.PaymentLinkResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("PAYMENT-SERVICE")
public interface PaymentFeignClient {
    @PostMapping("/api/payments/create")
    ResponseEntity<PaymentLinkResponse> createPaymentLink(@RequestBody BookingDto bookingDto, @RequestParam PaymentMethod paymentMethod, @RequestHeader("Authorization") String jwt);
}
