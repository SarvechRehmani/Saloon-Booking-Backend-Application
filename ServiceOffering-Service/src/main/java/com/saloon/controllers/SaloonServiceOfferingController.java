package com.saloon.controllers;

import com.saloon.payloads.dtos.CategoryDto;
import com.saloon.payloads.dtos.SaloonDto;
import com.saloon.payloads.dtos.ServiceDto;
import com.saloon.services.ServiceOfferingService;
import com.saloon.services.clients.CategoryFeignClient;
import com.saloon.services.clients.SaloonFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-offering/saloon-owner")
@RequiredArgsConstructor
public class SaloonServiceOfferingController {

    private final ServiceOfferingService serviceOfferingService;
    private final SaloonFeignClient saloonFeignClient;
    private final CategoryFeignClient categoryFeignClient;

    @PostMapping
    public ResponseEntity<ServiceDto> createService(@RequestBody ServiceDto serviceDto, @RequestHeader("Authorization") String jwt) {
        SaloonDto saloonDto = saloonFeignClient.getSaloonsByOwner(jwt).getBody();
        CategoryDto categoryDto = categoryFeignClient.getCategoryByIdAndSaloonId(serviceDto.getCategoryId(), saloonDto.getId()).getBody();
        if(categoryDto == null) {
            throw new RuntimeException("Category not found.");
        }
        return new ResponseEntity<>(serviceOfferingService.saveService(serviceDto, saloonDto, categoryDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceDto> updateService(@PathVariable Long id, @RequestBody ServiceDto serviceDto) {
        return ResponseEntity.ok(serviceOfferingService.updateService(serviceDto, id));
    }
}
