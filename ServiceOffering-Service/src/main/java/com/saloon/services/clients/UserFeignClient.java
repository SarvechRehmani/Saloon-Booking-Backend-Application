package com.saloon.services.clients;

import com.saloon.payloads.dtos.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("USER-SERVICE")
public interface UserFeignClient {
    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id);

    @GetMapping("/api/users/profile")
    public ResponseEntity<UserDto> getUserProfile(@RequestHeader("Authorization") String Jwt);
}
