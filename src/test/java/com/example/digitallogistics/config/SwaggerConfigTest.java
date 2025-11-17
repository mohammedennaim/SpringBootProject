package com.example.digitallogistics.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void customOpenAPI_ShouldReturnConfiguredOpenAPI() {
        SwaggerConfig swaggerConfig = new SwaggerConfig();
        
        OpenAPI openAPI = swaggerConfig.customOpenAPI();
        
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Digital Logistics API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getDescription());
        assertNotNull(openAPI.getInfo().getDescription());
        
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
    }
}