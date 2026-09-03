package com.saloon.payloads.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserProfileDto {
    @JsonProperty("given_name")
    private String firstName;
    @JsonProperty("family_name")
    private String lastName;
    private String email;
    @JsonProperty("preferred_username")
    private String username;
    @JsonProperty("sub")
    private String id;
    @JsonProperty("email_verified")
    private boolean emailVerified;
    @JsonProperty("name")
    private String fullName;

}
