package com.saloon.services.clients;

import com.saloon.dtos.SaloonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("SALOON-SERVICE")
public interface SaloonFeignClient {

    @GetMapping("/api/saloons/owner")
    ResponseEntity<SaloonDto> getSaloonsByOwner(@RequestHeader("Authorization")  String jwt);
    @GetMapping("/api/saloons/{id}")
    ResponseEntity<SaloonDto> getSaloonById(@PathVariable Long id);

}
