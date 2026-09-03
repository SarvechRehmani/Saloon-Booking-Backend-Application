package com.saloon.controllers;

import com.saloon.payloads.dtos.ServiceDto;
import com.saloon.services.ServiceOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/service-offering")

@RequiredArgsConstructor
public class ServiceOfferingController {
    private final ServiceOfferingService serviceOfferingService;


    @GetMapping("/saloon/{saloonId}")
    public ResponseEntity<Set<ServiceDto>> getServicesBySaloonId(@PathVariable Long saloonId,@RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(serviceOfferingService.getServicesBySaloonId(saloonId, categoryId));
    }
    @GetMapping("/list/{ids}")
    public ResponseEntity<Set<ServiceDto>> getServicesByIds(@PathVariable Set<Long> ids) {
        return ResponseEntity.ok(serviceOfferingService.getServicesByIds(ids));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDto> getServicesById(@PathVariable Long id) {
        return ResponseEntity.ok(serviceOfferingService.getServiceById(id));
    }


}
