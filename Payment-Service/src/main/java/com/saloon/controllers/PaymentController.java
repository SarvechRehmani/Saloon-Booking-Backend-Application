package com.saloon.controllers;

import com.saloon.domains.PaymentMethod;
import com.saloon.models.PaymentOrder;
import com.saloon.payloads.dtos.BookingDto;
import com.saloon.payloads.dtos.UserDto;
import com.saloon.payloads.response.PaymentLinkResponse;
import com.saloon.services.PaymentService;
import com.saloon.services.clients.UserFeignClient;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserFeignClient userFeignClient;

    @PostMapping("/create")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(@RequestBody BookingDto bookingDto, @RequestParam PaymentMethod paymentMethod, @RequestHeader("Authorization") String jwt) throws StripeException {
        UserDto userDto = this.userFeignClient.getUserProfile(jwt).getBody();
        PaymentLinkResponse response = paymentService.createOrder(userDto, bookingDto, paymentMethod);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{paymentOrderId}")
    public ResponseEntity<PaymentOrder> getPaymentOrderById(@PathVariable Long paymentOrderId) {
        return ResponseEntity.ok(paymentService.getPaymentOrderById(paymentOrderId));
    }

    @PatchMapping("/proceed")
    public ResponseEntity<Boolean> proceedPayment(@RequestParam String paymentId, @RequestParam String paymentLinkId) {
        PaymentOrder paymentOrder = paymentService.getPaymentOrderByPaymentId(paymentLinkId);
        return ResponseEntity.ok(paymentService.proceedPayment(paymentOrder, paymentId, paymentLinkId));
    }

}
