package com.saloon.controllers;

import com.saloon.domains.PaymentMethod;
import com.saloon.models.PaymentOrder;
import com.saloon.payloads.dtos.BookingDto;
import com.saloon.payloads.dtos.UserDto;
import com.saloon.payloads.response.PaymentLinkResponse;
import com.saloon.services.PaymentService;
import com.saloon.services.clients.UserFeignClient;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserFeignClient userFeignClient;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

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

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String stripeSignature) {
        final Event event;
        try {
            event = Webhook.constructEvent(payload, stripeSignature, stripeWebhookSecret);
        } catch (SignatureVerificationException | IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body("Invalid Stripe webhook signature or payload");
        }

        if ("checkout.session.completed".equals(event.getType())
                || "checkout.session.async_payment_succeeded".equals(event.getType())) {
            StripeObject stripeObject = event.getDataObjectDeserializer().getObject()
                    .orElse(null);
            if (!(stripeObject instanceof Session session)) {
                return ResponseEntity.badRequest().body("Stripe checkout session could not be read");
            }
            paymentService.processStripeCheckoutSession(session);
        }

        return ResponseEntity.ok("Webhook received");
    }

}
