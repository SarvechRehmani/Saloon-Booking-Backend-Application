package com.saloon.controllers;

import com.saloon.services.clients.UserFeignClient;
import com.saloon.mapper.SaloonMapper;
import com.saloon.models.Saloon;
import com.saloon.payloads.dtos.SaloonDto;
import com.saloon.payloads.dtos.UserDto;
import com.saloon.services.SaloonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saloons")

@RequiredArgsConstructor
public class SaloonController {

    private final SaloonService saloonService;
    private final UserFeignClient userFeignClient;

    @PostMapping
    public ResponseEntity<SaloonDto> createSaloon(@RequestBody SaloonDto saloonDto, @RequestHeader("Authorization") String jwt) {
        UserDto userDto=userFeignClient.getUserProfile(jwt).getBody();
        SaloonDto saloon =  this.saloonService.createSaloon(saloonDto,userDto);
        return new ResponseEntity<>(saloon, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SaloonDto>> getAllSaloons() {
        return ResponseEntity.ok(this.saloonService.getAllSaloons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaloonDto> getSaloonById(@PathVariable Long id) {
        return ResponseEntity.ok(this.saloonService.getSaloonById(id));
    }
    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<SaloonDto>> searchSaloon(@PathVariable String keyword) {
        return ResponseEntity.ok(this.saloonService.searchSaloons(keyword));
    }

    @GetMapping("/owner")
    public ResponseEntity<SaloonDto> getSaloonByOwnerId(@RequestHeader("Authorization") String jwt){
        UserDto userDto = userFeignClient.getUserProfile(jwt).getBody();
        if(userDto == null){
            throw new RuntimeException("User not found jwt.");
        }
        return ResponseEntity.ok(this.saloonService.getSaloonsByOwner(userDto.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaloonDto> updateSaloon(@PathVariable Long id, @RequestBody SaloonDto saloonDto, @RequestHeader("Authorization") String jwt) {
        UserDto userDto=userFeignClient.getUserProfile(jwt).getBody();
        return ResponseEntity.ok(this.saloonService.updateSaloon(id, saloonDto,userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSaloon(@PathVariable Long id) {
        this.saloonService.deleteSaloon(id);
        return ResponseEntity.ok("Saloon deleted successfully");
    }
}
