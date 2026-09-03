package com.saloon.payloads.response;

import com.saloon.domains.UserRole;
import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private String refreshToken;
    private String message;
    private String title;
    private UserRole role;
}
