package com.saloon.services.impl;

import com.saloon.Respositories.SaloonRepository;
import com.saloon.exceptions.BadRequestException;
import com.saloon.exceptions.ResourceNotFoundException;
import com.saloon.mapper.SaloonMapper;
import com.saloon.models.Saloon;
import com.saloon.payloads.dtos.SaloonDto;
import com.saloon.payloads.dtos.UserDto;
import com.saloon.services.SaloonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SaloonServiceImpl implements SaloonService {

    private final SaloonRepository saloonRepository;

    @Override
    public SaloonDto createSaloon(SaloonDto saloonDto, UserDto userDto) {
        Saloon saloon = new Saloon();
        saloon.setName(saloonDto.getName());
        saloon.setImages(saloonDto.getImages());
        saloon.setAddress(saloonDto.getAddress());
        saloon.setPhoneNumber(saloonDto.getPhoneNumber());
        saloon.setWebsite(saloonDto.getWebsite());
        saloon.setDescription(saloonDto.getDescription());
        saloon.setCity(saloonDto.getCity());
        saloon.setOwnerId(userDto.getId());
        saloon.setOpenTime(saloonDto.getOpenTime());
        saloon.setCloseTime(saloonDto.getCloseTime());
        return SaloonMapper.mapToSaloonDto(this.saloonRepository.save(saloon));
    }

    @Override
    public SaloonDto updateSaloon(Long saloonId, SaloonDto saloonDto, UserDto userDto) {
        SaloonDto existingSaloon = this.getSaloonById(saloonId);
        if(!Objects.equals(saloonDto.getId(), saloonId)){
            throw new BadRequestException("Saloon id not match");
        }
        if(existingSaloon.getOwnerId().equals(userDto.getId())){
            existingSaloon.setName(saloonDto.getName());
            existingSaloon.setImages(saloonDto.getImages());
            existingSaloon.setAddress(saloonDto.getAddress());
            existingSaloon.setPhoneNumber(saloonDto.getPhoneNumber());
            existingSaloon.setWebsite(saloonDto.getWebsite());
            existingSaloon.setDescription(saloonDto.getDescription());
            existingSaloon.setCity(saloonDto.getCity());
            existingSaloon.setOwnerId(userDto.getId());
            existingSaloon.setOpenTime(saloonDto.getOpenTime());
            existingSaloon.setCloseTime(saloonDto.getCloseTime());
        }else{
            throw new BadRequestException("You don't have permission to update this saloon");
        }
        Saloon saloon = SaloonMapper.mapToSaloon(existingSaloon);
        return SaloonMapper.mapToSaloonDto(saloonRepository.save(saloon));
    }

    @Override
    public List<SaloonDto> getAllSaloons() {
        List<Saloon> saloons = this.saloonRepository.findAll();
        if(saloons.isEmpty()){
            throw new ResourceNotFoundException("Saloons not found");
        }
        return saloons.stream().map(SaloonMapper::mapToSaloonDto).toList();
    }

    @Override
    public SaloonDto getSaloonById(Long saloonId) {
        Saloon saloon = this.saloonRepository.findById(saloonId)
                .orElseThrow(() -> new ResourceNotFoundException("Saloon not found with id : "+saloonId));
        return SaloonMapper.mapToSaloonDto(saloon);
    }

    @Override
    public SaloonDto getSaloonsByOwner(Long ownerId) {
        Saloon saloon = this.saloonRepository.findByOwnerId(
                ownerId).orElseThrow(() -> new ResourceNotFoundException("Saloon not found with owner id : "+ ownerId));
        return SaloonMapper.mapToSaloonDto(saloon);
    }

    @Override
    public List<SaloonDto> getSaloonByCity(String city) {
        List<Saloon> saloons = this.saloonRepository.findByCity(city);
        if(saloons.isEmpty()){
            throw new ResourceNotFoundException("Saloons not found by city : "+city);
        }
        return saloons.stream().map(SaloonMapper::mapToSaloonDto).toList();
    }

    @Override
    public List<SaloonDto> searchSaloons(String keyword) {
        List<Saloon> saloons = this.saloonRepository.searchSaloons(keyword);
        if(saloons.isEmpty()){
            throw new ResourceNotFoundException("Saloons not found with keyword : "+keyword);
        }
        return saloons.stream().map(SaloonMapper::mapToSaloonDto).toList();
    }

    @Override
    public void deleteSaloon(Long id) {
        Saloon saloon = SaloonMapper.mapToSaloon(this.getSaloonById(id));
        this.saloonRepository.delete(saloon);
    }

}
