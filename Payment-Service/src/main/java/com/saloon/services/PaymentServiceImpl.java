package com.saloon.services;

import com.saloon.domains.PaymentMethod;
import com.saloon.domains.PaymentOrderStatus;
import com.saloon.messaging.BookingEventProducer;
import com.saloon.messaging.NotificationEventProducer;
import com.saloon.models.PaymentOrder;
import com.saloon.payloads.dtos.BookingDto;
import com.saloon.payloads.dtos.UserDto;
import com.saloon.payloads.response.PaymentLinkResponse;
import com.saloon.repositories.PaymentOrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final BookingEventProducer bookingEventProducer;
    private final NotificationEventProducer notificationEventProducer;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    @Override
    public PaymentLinkResponse createOrder(UserDto userDto, BookingDto bookingDto, PaymentMethod paymentMethod) throws StripeException {
        Long amount = bookingDto.getTotalPrice();
        PaymentOrder order = new PaymentOrder();
        order.setAmount(amount);
        order.setPaymentMethod(paymentMethod);
        order.setUserId(userDto.getId());
        order.setBookingId(bookingDto.getId());
        order.setSaloonId(bookingDto.getSaloonId());
        PaymentOrder savedOrder = paymentOrderRepository.save(order);
        PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();
        if (paymentMethod.equals(PaymentMethod.STRIPE)) {
            Session session = createStripeCheckoutSession(savedOrder.getAmount(), savedOrder.getId());
            savedOrder.setPaymentLinkId(session.getId());
            paymentOrderRepository.save(savedOrder);
            paymentLinkResponse.setPayment_link_url(session.getUrl());
            paymentLinkResponse.setPayment_link_id(session.getId());
        }
        return paymentLinkResponse;
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) {
        return paymentOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment Order not found with id : " + id));
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String paymentLinkId) {
        return paymentOrderRepository.findByPaymentLinkId(paymentLinkId).orElseThrow(
                () -> new RuntimeException("Payment Order not found with payment link id : " + paymentLinkId));
    }

    @Override
    public Session createStripeCheckoutSession(Long amount, Long orderId) throws StripeException {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new IllegalStateException("Stripe API key is not configured");
        }
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/payment-success/" + orderId)
                .setCancelUrl("http://localhost:3000/payment-cancel/" + orderId)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amount * 100L)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Saloon Appointment Booking")
                                        .build())
                                .build())
                        .build())
                .build();
        return Session.create(params);
    }

    @Override
    @Transactional
    public void processStripeCheckoutSession(Session session) {
        if (stripeWebhookSecret == null || stripeWebhookSecret.isBlank()) {
            throw new IllegalStateException("Stripe webhook secret is not configured");
        }
        if (!"paid".equals(session.getPaymentStatus())) {
            return;
        }

        PaymentOrder paymentOrder = paymentOrderRepository.findByPaymentLinkId(session.getId())
                .orElseThrow(() -> new IllegalArgumentException("No payment order matches the Stripe checkout session"));

        long expectedAmountInCents = Math.multiplyExact(paymentOrder.getAmount(), 100L);
        if (!Long.valueOf(expectedAmountInCents).equals(session.getAmountTotal())
                || !"usd".equalsIgnoreCase(session.getCurrency())) {
            throw new IllegalArgumentException("Stripe checkout session amount or currency does not match the payment order");
        }

        if (paymentOrder.getStatus() != PaymentOrderStatus.PENDING
                || paymentOrder.getPaymentMethod() != PaymentMethod.STRIPE) {
            return;
        }

        bookingEventProducer.sendBookingUpdateEvent(paymentOrder);
        notificationEventProducer.sendNotificationEvent(
                paymentOrder.getBookingId(), paymentOrder.getUserId(), paymentOrder.getSaloonId());

        paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
        paymentOrderRepository.save(paymentOrder);
    }
}
