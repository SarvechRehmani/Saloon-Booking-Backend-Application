package com.saloon.services;

import com.saloon.payloads.dtos.*;
import com.saloon.payloads.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final RestTemplate restTemplate;

    @Value("${keycloak.base-url:http://localhost:8080}")
    private String baseUrl;
    @Value("${keycloak.realm:saloon}")
    private String realm;
    @Value("${keycloak.client-id:saloon-booking-client}")
    private String clientId;
    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;
    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    public void createUser(SignUpDto signUpDto) {
        String adminToken = getAdminAccessToken();
        String realmAdminUrl = baseUrl + "/admin/realms/" + realm;

        Credential credential = new Credential("password", signUpDto.getPassword(), false);
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName(signUpDto.getFirstName());
        userRequest.setLastName(signUpDto.getLastName());
        userRequest.setEnabled(true);
        userRequest.setEmail(signUpDto.getEmail());
        userRequest.setUsername(signUpDto.getUsername());
        userRequest.setCredentials(List.of(credential));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        ResponseEntity<String> response = restTemplate.exchange(
                realmAdminUrl + "/users", HttpMethod.POST,
                new HttpEntity<>(userRequest, headers), String.class);

        if (response.getStatusCode() != HttpStatus.CREATED) {
            throw new RuntimeException("Keycloak user creation failed: " + response.getBody());
        }

        KeycloakUserDto user = fetchFirstUserByUsername(signUpDto.getUsername(), adminToken);
        KeycloakRole role = getRoleByName(signUpDto.getRole().toString(), adminToken);
        assignRoleToUser(user.getId(), List.of(role), adminToken);
    }

    public TokenResponse getUserAccessToken(String username, String password, String grantType, String refreshToken) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", grantType);
        if (username != null) requestBody.add("username", username);
        if (password != null) requestBody.add("password", password);
        if (refreshToken != null) requestBody.add("refresh_token", refreshToken);
        requestBody.add("client_id", clientId);
        requestBody.add("scope", "openid profile email");
        return requestToken(baseUrl + "/realms/" + realm + "/protocol/openid-connect/token", requestBody);
    }

    private String getAdminAccessToken() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("username", adminUsername);
        requestBody.add("password", adminPassword);
        requestBody.add("client_id", "admin-cli");
        return requestToken(baseUrl + "/realms/master/protocol/openid-connect/token", requestBody).getAccessToken();
    }

    private TokenResponse requestToken(String tokenUrl, MultiValueMap<String, String> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                tokenUrl, HttpMethod.POST, new HttpEntity<>(requestBody, headers), TokenResponse.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Failed to obtain Keycloak access token.");
    }

    public KeycloakRole getRoleByName(String role, String token) {
        String url = baseUrl + "/admin/realms/" + realm + "/roles/" + role;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), KeycloakRole.class).getBody();
    }

    public KeycloakUserDto fetchFirstUserByUsername(String username, String token) {
        String url = baseUrl + "/admin/realms/" + realm + "/users?username=" + username;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<KeycloakUserDto[]> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), KeycloakUserDto[].class);
        KeycloakUserDto[] users = response.getBody();
        if (users != null && users.length > 0) return users[0];
        throw new RuntimeException("User not found with username: " + username);
    }

    public void assignRoleToUser(String userId, List<KeycloakRole> roles, String token) {
        String url = baseUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(roles, headers), String.class);
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to assign Keycloak role to user.", e);
        }
    }

    public UserProfileDto fetchUserProfileByJwt(String token) {
        String url = baseUrl + "/realms/" + realm + "/protocol/openid-connect/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), UserProfileDto.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch user profile.", e);
        }
    }
}
