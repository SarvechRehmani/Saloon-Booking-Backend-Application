package com.saloon.configurations;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            List<String> realmRoles = (List<String>) realmAccess.get("roles");

            realmRoles.forEach(role ->
                    authorities.add(new SimpleGrantedAuthority( "ROLE_" + role.toUpperCase()))
            );
        }

        Map<String, Object> resourceAccess =jwt.getClaimAsMap("resource_access");

        if (resourceAccess != null) {
            resourceAccess.forEach((client, clientDetails) -> {
                Map<String, Object> clientDetailsMap = (Map<String, Object>) clientDetails;
                if (clientDetailsMap.containsKey("roles")) {
                    List<String> clientRoles = (List<String>) clientDetailsMap.get("roles");
                    clientRoles.forEach(role ->
                            authorities.add(new SimpleGrantedAuthority( "ROLE_" + role.toUpperCase()))
                    );
                }
            });
        }
        return authorities;
    }
}
