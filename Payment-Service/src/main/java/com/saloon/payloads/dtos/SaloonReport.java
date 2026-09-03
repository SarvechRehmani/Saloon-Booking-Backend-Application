package com.saloon.payloads.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaloonReport {
    private Long saloonId;
    private String saloonName;
    private Double totalEarning;
    private Integer totalBookings;
    private Integer cancelBookings;
    private Double totalRefund;
}
