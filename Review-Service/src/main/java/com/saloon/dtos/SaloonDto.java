package com.saloon.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonDto {
    private Long id;
    private String name;
    private List<String> images;
    private String address;
    private String phoneNumber;
    private String website;
    private String description;
    private String city;
    private Long OwnerId;
    private LocalTime openTime;
    private LocalTime closeTime;
}
