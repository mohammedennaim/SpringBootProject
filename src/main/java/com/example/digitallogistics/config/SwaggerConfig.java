package com.example.digitallogistics.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import java.net.URI;

@Configuration
public class SwaggerConfig {

        @Value("${server.port:8093}")
        private String serverPort;

        @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:http://localhost:8080/auth/realms/logistics-realm}")
        private String issuerUri;

    @Bean
    public OpenAPI customOpenAPI() {
                String serverUrl = "http://localhost:" + serverPort;

                // Construit les URLs auth/token à partir de l'issuer configuré pour éviter les divergences
                String normalizedIssuer = issuerUri.endsWith("/") ? issuerUri.substring(0, issuerUri.length() - 1) : issuerUri;
                URI issuer = URI.create(normalizedIssuer);
                String authBase = issuer.toString();
                String authorizationUrl = authBase + "/protocol/openid-connect/auth";
                String tokenUrl = authBase + "/protocol/openid-connect/token";
        
        Components components = new Components();
        // OAuth2 - le token obtenu sera automatiquement utilisé dans toutes les requêtes
        components.addSecuritySchemes("OAuth2",
                new SecurityScheme()
                        .type(SecurityScheme.Type.OAUTH2)
                        .description("Authentification OAuth2 via Keycloak. Le token sera automatiquement utilisé après l'authentification.")
                        .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                                .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                                        .authorizationUrl(authorizationUrl)
                                        .tokenUrl(tokenUrl)
                                        .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                .addString("openid", "OpenID Connect")
                                                .addString("profile", "Profile information")
                                                .addString("email", "Email address")
                                                .addString("roles", "User roles")))));
        // Bearer Authentication comme option alternative (pour coller un token manuellement)
        components.addSecuritySchemes("Bearer Authentication", 
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Option alternative : Collez votre token JWT Keycloak manuellement"));
        
        return new OpenAPI()
                .info(new Info()
                        .title("Digital Logistics API")
                        .description("API de gestion logistique avec PostgreSQL et authentification Keycloak OAuth2")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Digital Logistics Team")
                                .email("support@digitallogistics.com")))
                .servers(List.of(
                        new Server().url(serverUrl).description("Serveur local")
                ))
                // OAuth2 comme SecurityRequirement principal - le token sera automatiquement utilisé
                .addSecurityItem(new SecurityRequirement().addList("OAuth2"))
                .components(components);
    }
}