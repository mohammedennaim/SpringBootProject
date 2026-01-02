package com.example.digitallogistics.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Configuration personnalisée pour JwtDecoder.
 * Permet de définir un hôte JWKS différent (ex: keycloak:8080 dans Docker) via keycloak.jwks-host,
 * tout en validant toujours l'issuer avec l'URL publique configurée.
 */
@Configuration
public class JwtDecoderConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${keycloak.jwks-host:}")
    private String jwksHost;

    @Bean
    public JwtDecoder jwtDecoder() {
        // Construire l'URL JWKS. Par défaut on utilise issuerUri, mais on peut remplacer l'hôte
        // (ex: keycloak:8080 dans Docker) via la propriété keycloak.jwks-host.
        String sanitizedIssuer = issuerUri.endsWith("/") ? issuerUri.substring(0, issuerUri.length() - 1) : issuerUri;
        String jwksUri;
        if (jwksHost != null && !jwksHost.isBlank()) {
            URI issuer = URI.create(sanitizedIssuer);
            String path = issuer.getPath().endsWith("/") ? issuer.getPath() : issuer.getPath() + "/";
            jwksUri = issuer.getScheme() + "://" + jwksHost + path + "protocol/openid-connect/certs";
        } else {
            jwksUri = sanitizedIssuer + "/protocol/openid-connect/certs";
        }
        
        // Créer le decoder avec l'URL JWKS interne
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwksUri)
                .jwsAlgorithm(org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.RS256)
                .build();
        
        // Valider l'issuer avec l'URL publique (localhost:8080)
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuerUri));
        
        return decoder;
    }
}

