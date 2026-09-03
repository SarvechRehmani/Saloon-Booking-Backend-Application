package com.saloon.controllers;

import com.saloon.payloads.dtos.LoginDto;
import com.saloon.payloads.dtos.SignUpDto;
import com.saloon.payloads.response.AuthResponse;
import com.saloon.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignUpDto signUpDto) {
        return ResponseEntity.ok(authService.signup(signUpDto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(authService.login(loginDto.getUsername(), loginDto.getPassword()));
    }

    @GetMapping("/access-token/refresh-token/{refreshToken}")
    public ResponseEntity<AuthResponse> getAccessTokenFromRefreshToken(@PathVariable String refreshToken) {
        return ResponseEntity.ok(authService.getAccessTokenFromRefreshToken(refreshToken));
    }
}
