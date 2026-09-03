package com.saloon.services;

import com.saloon.domains.PaymentMethod;
import com.saloon.models.PaymentOrder;
import com.saloon.payloads.dtos.BookingDto;
import com.saloon.payloads.dtos.UserDto;
import com.saloon.payloads.response.PaymentLinkResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentLink;

public interface PaymentService {

    PaymentLinkResponse createOrder(UserDto userDto, BookingDto bookingDto, PaymentMethod paymentMethod) throws StripeException;

    PaymentOrder getPaymentOrderById(Long id);
    PaymentOrder getPaymentOrderByPaymentId(String paymentLinkId);
//    PaymentLink createRazorPaymentLink(UserDto userDto, Long amount, Long orderId);
    String createStripePaymentLink(UserDto userDto, Long Long, Long orderId) throws StripeException;
    Boolean proceedPayment(PaymentOrder paymentOrder, String paymentId, String paymentLinkId);
}
