package com.saloon.mappers;

import com.saloon.models.ServiceOffering;
import com.saloon.payloads.dtos.ServiceDto;

public class ServiceOfferingMapper {
    public static ServiceDto toServiceDto(ServiceOffering serviceOffering) {
        ServiceDto dto = new ServiceDto();
        dto.setId(serviceOffering.getId());
        dto.setName(serviceOffering.getName());
        dto.setImage(serviceOffering.getImage());
        dto.setDescription(serviceOffering.getDescription());
        dto.setDuration(serviceOffering.getDuration());
        dto.setCategoryId(serviceOffering.getCategoryId());
        dto.setPrice(serviceOffering.getPrice());
        dto.setSaloonId(serviceOffering.getSaloonId());
        return dto;
    }

    public static ServiceOffering toServiceOffering(ServiceDto serviceDto) {
        ServiceOffering offering = new ServiceOffering();
        offering.setId(serviceDto.getId());
        offering.setName(serviceDto.getName());
        offering.setImage(serviceDto.getImage());
        offering.setDescription(serviceDto.getDescription());
        offering.setDuration(serviceDto.getDuration());
        offering.setCategoryId(serviceDto.getCategoryId());
        offering.setPrice(serviceDto.getPrice());
        offering.setSaloonId(serviceDto.getSaloonId());
        return offering;
    }
}
