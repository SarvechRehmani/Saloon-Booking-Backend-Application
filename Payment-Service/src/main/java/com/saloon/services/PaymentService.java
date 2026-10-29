package com.saloon.services;

import com.saloon.domains.PaymentMethod;
import com.saloon.models.PaymentOrder;
import com.saloon.payloads.dtos.BookingDto;
import com.saloon.payloads.dtos.UserDto;
import com.saloon.payloads.response.PaymentLinkResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

public interface PaymentService {

    PaymentLinkResponse createOrder(UserDto userDto, BookingDto bookingDto, PaymentMethod paymentMethod) throws StripeException;

    PaymentOrder getPaymentOrderById(Long id);
    PaymentOrder getPaymentOrderByPaymentId(String paymentLinkId);
    Session createStripeCheckoutSession(Long amount, Long orderId) throws StripeException;
    void processStripeCheckoutSession(Session session);
}
