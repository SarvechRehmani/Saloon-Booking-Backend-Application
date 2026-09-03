package com.saloon.services;

import com.saloon.payloads.dtos.*;
import com.saloon.payloads.response.TokenResponse;
import lombok.RequiredArgsConstructor;
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

    private static final String KEYCLOAK_BASE_URL = "http://localhost:8080";
    private static final String KEYCLOAK_ADMIN_API = KEYCLOAK_BASE_URL+"/admin/realms/master/users";
    private static final String TOKEN_URL = KEYCLOAK_BASE_URL+"/realms/master/protocol/openid-connect/token";
    private static final String CLIENT_ID = "saloon-booking-client";
    private static final String CLIENT_SECRET = "spRwbrMHlYI3yAetdlmBFeT16g9RfNb96jjqLdKKYC7XQ5hMHfSAcgQkm5FshVFsNthjlqmlmFFttzN7YgJbYM";
    private static final String GRANT_TYPE = "password";
    private static final String SCOPE = "openid profile email";
    private static final String username = "sarvech";
    private static final String password = "admin";
    private static final String clientId = "9af0d13b-3dc3-4598-aa8f-dff9db48901c";

    private final RestTemplate restTemplate;

    public void createUser(SignUpDto signUpDto){

        String ACCESS_TOKEN = getAdminAccessToken(username, password, GRANT_TYPE, null).getAccessToken();

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
        headers.setBearerAuth(ACCESS_TOKEN);

        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(userRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(KEYCLOAK_ADMIN_API, HttpMethod.POST, requestEntity, String.class);

        if(response.getStatusCode() == HttpStatus.CREATED){
            System.out.println("User created successfully");
            KeycloakUserDto user = fetchFirstUserByUsername(signUpDto.getUsername(), ACCESS_TOKEN);
            List<KeycloakRole> roles = List.of(getRoleByName(clientId, ACCESS_TOKEN, signUpDto.getRole().toString()));
            assignRoleToUser(user.getId(), clientId, roles, ACCESS_TOKEN);
        }else{
            System.out.println("User creation failed");
            throw new RuntimeException(response.getBody());
        }
    }

    public TokenResponse getAdminAccessToken(String username, String password, String grandType, String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", grandType);
        requestBody.add("username", username);
        requestBody.add("password", password);
        requestBody.add("refresh_token", refreshToken);
        requestBody.add("client_id", CLIENT_ID);
        requestBody.add("client_secret", CLIENT_SECRET);
        requestBody.add("scope", SCOPE);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<TokenResponse> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, requestEntity, TokenResponse.class);
        if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }
        throw new RuntimeException("Fail to obtain access token.");
    }

    public KeycloakRole getRoleByName(String clientId, String token, String role) {
        String url = KEYCLOAK_BASE_URL + "/admin/realms/master/clients/"+clientId+"/roles/"+role;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<KeycloakRole> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, KeycloakRole.class);
        return response.getBody();
    }

    public KeycloakUserDto fetchFirstUserByUsername(String username, String token) {
        String url = KEYCLOAK_BASE_URL + "/admin/realms/master/users?username="+username;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<KeycloakUserDto[]> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, KeycloakUserDto[].class);

        KeycloakUserDto[] users = response.getBody();

        if (users != null && users.length > 0) {
            return users[0];
        }
        throw new RuntimeException("User not found with username : "+username);
    }

    public void assignRoleToUser(String userId, String clientId, List<KeycloakRole> roles, String token){
        String url = KEYCLOAK_BASE_URL + "/admin/realms/master/users/"+userId+"/role-mappings/clients/"+clientId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<KeycloakRole>> requestEntity = new HttpEntity<>(roles,headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            System.out.println(response.getStatusCode());

            if(response.getStatusCode() == HttpStatus.OK){
                System.out.println("Role assigned successfully");

            }
        }catch (HttpClientErrorException e){
            throw new RuntimeException("Fail to assign role to user.");
        }
    }


    public UserProfileDto fetchUserProfileByJwt(String token){
        String url = KEYCLOAK_BASE_URL + "/realms/master/protocol/openid-connect/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try{
            ResponseEntity<UserProfileDto> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, UserProfileDto.class);
            return response.getBody();
        }catch (HttpClientErrorException  e){
            throw new RuntimeException("Fail to fetch user profile.");
        }
    }
}
