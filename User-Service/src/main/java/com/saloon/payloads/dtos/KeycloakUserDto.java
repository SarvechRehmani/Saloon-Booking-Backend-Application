package com.saloon.payloads.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakUserDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
}
