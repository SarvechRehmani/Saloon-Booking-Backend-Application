package com.saloon.mapper;

import com.saloon.models.Saloon;
import com.saloon.payloads.dtos.SaloonDto;

public class SaloonMapper {

    public static SaloonDto mapToSaloonDto(Saloon saloon){
        SaloonDto dto = new SaloonDto();
        dto.setId(saloon.getId());
        dto.setName(saloon.getName());
        dto.setImages(saloon.getImages());
        dto.setAddress(saloon.getAddress());
        dto.setPhoneNumber(saloon.getPhoneNumber());
        dto.setWebsite(saloon.getWebsite());
        dto.setDescription(saloon.getDescription());
        dto.setCity(saloon.getCity());
        dto.setOwnerId(saloon.getOwnerId());
        dto.setOpenTime(saloon.getOpenTime());
        dto.setCloseTime(saloon.getCloseTime());
        return dto;
    }

    public static Saloon mapToSaloon(SaloonDto saloonDto){
        Saloon saloon = new Saloon();
        saloon.setId(saloonDto.getId());
        saloon.setName(saloonDto.getName());
        saloon.setImages(saloonDto.getImages());
        saloon.setAddress(saloonDto.getAddress());
        saloon.setPhoneNumber(saloonDto.getPhoneNumber());
        saloon.setWebsite(saloonDto.getWebsite());
        saloon.setDescription(saloonDto.getDescription());
        saloon.setCity(saloonDto.getCity());
        saloon.setOwnerId(saloonDto.getOwnerId());
        saloon.setOpenTime(saloonDto.getOpenTime());
        saloon.setCloseTime(saloonDto.getCloseTime());
        return saloon;
    }
}
