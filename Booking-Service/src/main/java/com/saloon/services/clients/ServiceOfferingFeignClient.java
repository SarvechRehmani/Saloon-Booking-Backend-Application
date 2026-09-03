package com.saloon.services.clients;

import com.saloon.dtos.ServiceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Set;

@FeignClient("SERVICEOFFERING-SERVICE")
public interface ServiceOfferingFeignClient {

    @GetMapping("/api/service-offering/list/{ids}")
    ResponseEntity<Set<ServiceDto>> getServicesByIds(@PathVariable Set<Long> ids);
}
