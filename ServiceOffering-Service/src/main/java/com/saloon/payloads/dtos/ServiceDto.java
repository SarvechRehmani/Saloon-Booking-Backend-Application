package com.saloon.payloads.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDto {
    private Long id;
    private String name;
    private String description;
    private int price;
    private int duration;
    private Long saloonId;
    private Long categoryId;
    private String image;
}
