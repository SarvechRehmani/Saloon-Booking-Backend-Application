package com.saloon.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Saloon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ElementCollection
    private List<String> images;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String phoneNumber;
    private String website;
    private String description;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private Long ownerId;
    @Column(nullable = false)
    private LocalTime openTime;
    @Column(nullable = false)
    private LocalTime closeTime;

}
