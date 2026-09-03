package com.saloon.services;

import com.saloon.payloads.dtos.CategoryDto;
import com.saloon.payloads.dtos.SaloonDto;
import com.saloon.payloads.dtos.ServiceDto;

import java.util.List;
import java.util.Set;

public interface ServiceOfferingService {

    ServiceDto saveService(ServiceDto serviceDto, SaloonDto saloonDto, CategoryDto categoryDto);
    ServiceDto updateService(ServiceDto serviceDto, Long serviceId);
    Set<ServiceDto> getServicesBySaloonId(Long saloonId, Long categoryId);
    Set<ServiceDto> getServicesByIds(Set<Long> ids);
    ServiceDto getServiceById(Long id);
}
