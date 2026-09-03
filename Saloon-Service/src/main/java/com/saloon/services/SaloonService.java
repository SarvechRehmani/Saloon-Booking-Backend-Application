package com.saloon.services;

import com.saloon.payloads.dtos.SaloonDto;
import com.saloon.payloads.dtos.UserDto;

import java.util.List;

public interface SaloonService {

    SaloonDto createSaloon(SaloonDto saloonDto, UserDto userDto);
    SaloonDto updateSaloon(Long saloonId, SaloonDto saloonDto, UserDto user);
    List<SaloonDto> getAllSaloons();
    SaloonDto getSaloonById(Long saloonId);
    SaloonDto getSaloonsByOwner(Long ownerId);
    List<SaloonDto> getSaloonByCity(String city);
    List<SaloonDto> searchSaloons(String keyword);
    void deleteSaloon(Long id);
}
