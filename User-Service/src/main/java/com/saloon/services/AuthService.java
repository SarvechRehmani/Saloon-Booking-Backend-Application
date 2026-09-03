package com.saloon.services;

import com.saloon.payloads.dtos.SignUpDto;
import com.saloon.payloads.dtos.UserRequest;
import com.saloon.payloads.response.AuthResponse;

public interface AuthService {
    AuthResponse login(String username, String password);
    AuthResponse signup(SignUpDto signUpDto);
    AuthResponse getAccessTokenFromRefreshToken(String refreshToken);
}
