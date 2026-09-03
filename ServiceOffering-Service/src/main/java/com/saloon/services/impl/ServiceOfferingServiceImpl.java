package com.saloon.services.impl;

import com.saloon.mappers.ServiceOfferingMapper;
import com.saloon.models.ServiceOffering;
import com.saloon.payloads.dtos.CategoryDto;
import com.saloon.payloads.dtos.SaloonDto;
import com.saloon.payloads.dtos.ServiceDto;
import com.saloon.repositories.ServiceOfferingRepository;
import com.saloon.services.ServiceOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceOfferingServiceImpl implements ServiceOfferingService {
    private final ServiceOfferingRepository serviceOfferingRepository;

    @Override
    public ServiceDto saveService(ServiceDto serviceDto, SaloonDto saloonDto, CategoryDto categoryDto) {
        ServiceOffering offering = ServiceOfferingMapper.toServiceOffering(serviceDto);
        offering.setSaloonId(saloonDto.getId());
        offering.setCategoryId(categoryDto.getId());
        return ServiceOfferingMapper.toServiceDto(serviceOfferingRepository.save(offering));
    }

    @Override
    public ServiceDto updateService(ServiceDto serviceDto, Long serviceId) {
        ServiceOffering existingService = serviceOfferingRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found with id : "+serviceId));
        if(!serviceDto.getId().equals(serviceId)) {
            throw new RuntimeException("ID mismatch");
        }
        existingService.setName(serviceDto.getName());
        existingService.setImage(serviceDto.getImage());
        existingService.setDescription(serviceDto.getDescription());
        existingService.setDuration(serviceDto.getDuration());
        existingService.setPrice(serviceDto.getPrice());
        return ServiceOfferingMapper.toServiceDto(serviceOfferingRepository.save(existingService));
    }

    @Override
    public Set<ServiceDto> getServicesBySaloonId(Long saloonId, Long categoryId) {
        Set<ServiceOffering> services = serviceOfferingRepository.findBySaloonId(saloonId);
        if(categoryId != null){
            services = services.stream()
                    .filter(s ->  s.getCategoryId() != null && s.getCategoryId().equals(categoryId))
                    .collect(Collectors.toSet());
        }
        return services.stream().map(ServiceOfferingMapper::toServiceDto).collect(Collectors.toSet());
    }

    @Override
    public Set<ServiceDto> getServicesByIds(Set<Long> ids) {
        return serviceOfferingRepository.findAllById(ids)
                .stream()
                .map(ServiceOfferingMapper::toServiceDto)
                .collect(Collectors.toSet());
    }

    @Override
    public ServiceDto getServiceById(Long id) {
        return serviceOfferingRepository.findById(id)
                .map(ServiceOfferingMapper::toServiceDto)
                .orElseThrow(() -> new RuntimeException("Service not found with id : " + id));
    }
}
