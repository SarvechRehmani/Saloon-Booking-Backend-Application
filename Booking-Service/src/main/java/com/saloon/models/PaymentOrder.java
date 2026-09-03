package com.saloon.models;

import com.saloon.domains.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    private String paymentLinkId;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long bookingId;
    @Column(nullable = false)
    private Long saloonId;
}
