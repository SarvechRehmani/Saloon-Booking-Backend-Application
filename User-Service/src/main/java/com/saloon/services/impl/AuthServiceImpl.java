package com.saloon.services.impl;

import com.saloon.models.User;
import com.saloon.payloads.dtos.SignUpDto;
import com.saloon.payloads.dtos.UserRequest;
import com.saloon.payloads.response.AuthResponse;
import com.saloon.payloads.response.TokenResponse;
import com.saloon.services.AuthService;
import com.saloon.services.KeycloakService;
import com.saloon.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final KeycloakService keycloakService;


    @Override
    public AuthResponse login(String username, String password) {

        TokenResponse tokenResponse = keycloakService.getAdminAccessToken(username, password,"password",null);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(tokenResponse.getAccessToken());
        authResponse.setRefreshToken(tokenResponse.getRefreshToken());
        authResponse.setMessage("Login success");
        return authResponse;
    }

    @Override
    public AuthResponse signup(SignUpDto signUpDto) {
        keycloakService.createUser(signUpDto);
        User user = new User();
        user.setFullName(signUpDto.getFirstName()+" "+signUpDto.getLastName());
        user.setEmail(signUpDto.getEmail());
        user.setUsername(signUpDto.getUsername());
        user.setPassword(signUpDto.getPassword());
        user.setRole(signUpDto.getRole());
        user.setCreatedAt(LocalDateTime.now());
        userService.createUser(user);

        TokenResponse tokenResponse = keycloakService.getAdminAccessToken(signUpDto.getUsername(),signUpDto.getPassword(),"password",null);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(tokenResponse.getAccessToken());
        authResponse.setRefreshToken(tokenResponse.getRefreshToken());
        authResponse.setRole(user.getRole());
        authResponse.setMessage("Register success");
        return authResponse;
    }

    @Override
    public AuthResponse getAccessTokenFromRefreshToken(String refreshToken) {
        TokenResponse tokenResponse = keycloakService.getAdminAccessToken(null, null,"refresh_token",refreshToken);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(tokenResponse.getAccessToken());
        authResponse.setRefreshToken(tokenResponse.getRefreshToken());
        authResponse.setMessage("Access Token re-generated");
        return authResponse;
    }
}
