package com.saloon.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String reviewText;
    @Column(nullable = false)
    private double rating;
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private Long saloonId;
    @CreatedDate
    private LocalDateTime createdAt;
}
