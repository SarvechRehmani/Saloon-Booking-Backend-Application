package com.saloon.services.clients;

import com.saloon.payloads.dtos.SaloonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("SALOON-SERVICE")
public interface SaloonFeignClient {

    @GetMapping("/api/saloons/owner")
    public ResponseEntity<SaloonDto> getSaloonsByOwner(@RequestHeader("Authorization")  String jwt);

}
