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

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8093}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        String serverUrl = "http://localhost:" + serverPort;
        String keycloakUrl = "http://localhost:8080/auth";
        String realm = "logistics-realm";
        
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
                // Support pour Bearer Token (JWT) - méthode manuelle
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                // Support pour OAuth2 - méthode interactive dans Swagger UI
                .addSecurityItem(new SecurityRequirement().addList("OAuth2"))
                .components(new Components()
                        // Schéma Bearer Token (pour coller un token manuellement)
                        .addSecuritySchemes("Bearer Authentication", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Collez votre token JWT Keycloak ici"))
                        // Schéma OAuth2 (pour authentification interactive)
                        .addSecuritySchemes("OAuth2",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .description("Authentification OAuth2 via Keycloak (support Google si configuré)")
                                        .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                                                .authorizationCode(new io.swagger.v3.oas.models.security.OAuthFlow()
                                                        .authorizationUrl(keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/auth")
                                                        .tokenUrl(keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                                                        .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                                .addString("openid", "OpenID Connect")
                                                                .addString("profile", "Profile information")
                                                                .addString("email", "Email address")
                                                                .addString("roles", "User roles"))))));
    }
}