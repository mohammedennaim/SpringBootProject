package com.example.digitallogistics.security;

import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convertit les JWT Keycloak en Authentication Spring Security.
 * Extrait les rôles depuis le claim "roles" ou "realm_access.roles" et les préfixe avec "ROLE_".
 * 
 * Ce converter gère deux formats de rôles Keycloak :
 * 1. Claim direct "roles" : ["ADMIN", "USER"]
 * 2. Claim "realm_access.roles" : {"realm_access": {"roles": ["ADMIN", "USER"]}}
 */
@Component
@Profile("!test")  // Ne pas charger en mode test
public class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Essayer d'abord le claim direct "roles" (format préféré selon le TODO)
        List<String> roles = jwt.getClaimAsStringList("roles");
        
        // Si pas trouvé, essayer "realm_access.roles" (format standard Keycloak)
        if (roles == null || roles.isEmpty()) {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null) {
                @SuppressWarnings("unchecked")
                List<String> realmRoles = (List<String>) realmAccess.get("roles");
                if (realmRoles != null) {
                    roles = realmRoles;
                }
            }
        }
        
        // Si toujours pas trouvé, retourner une liste vide
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Convertir les rôles en authorities avec le préfixe ROLE_
        return roles.stream()
                .map(role -> {
                    // Enlever le préfixe ROLE_ s'il existe déjà pour éviter ROLE_ROLE_ADMIN
                    String cleanRole = role.startsWith("ROLE_") ? role.substring(5) : role;
                    return new SimpleGrantedAuthority("ROLE_" + cleanRole);
                })
                .collect(Collectors.toList());
    }
}


