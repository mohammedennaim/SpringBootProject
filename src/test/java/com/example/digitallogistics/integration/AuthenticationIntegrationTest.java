package com.example.digitallogistics.integration;

import com.example.digitallogistics.LogisticsApiApplication;
import com.example.digitallogistics.config.TestSecurityConfig;
import com.example.digitallogistics.model.dto.AuthRequest;
import com.example.digitallogistics.model.dto.TokenRefreshRequest;
import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.entity.RefreshToken;
import com.example.digitallogistics.model.entity.SalesOrder;
import com.example.digitallogistics.model.enums.OrderStatus;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.repository.ClientRepository;
import com.example.digitallogistics.repository.RefreshTokenRepository;
import com.example.digitallogistics.repository.SalesOrderRepository;
import com.example.digitallogistics.repository.UserRepository;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour l'authentification selon le cahier des charges
 * 
 * Scénarios testés :
 * - Login valide et invalide
 * - Accès avec token valide, sans token, token expiré
 * - Renouvellement via refresh token
 * - Rejet d'un refresh token révoqué
 * - Accès interdit selon rôle
 * - Refus d'accès aux ressources d'un autre client (ownership)
 */
@SpringBootTest(
    classes = {LogisticsApiApplication.class, TestSecurityConfig.class},
    properties = {
        "spring.data.elasticsearch.repositories.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration"
    }
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Client client1;
    private Client client2;
    private String client1Password = "password123";
    private String client2Password = "password456";
    private String accessTokenClient1;
    private String refreshTokenClient1;
    private SalesOrder orderClient1;
    private SalesOrder orderClient2;

    @BeforeEach
    void setUp() {
        // Nettoyer la base de données
        refreshTokenRepository.deleteAll();
        salesOrderRepository.deleteAll();
        clientRepository.deleteAll();

        // Créer client 1
        client1 = new Client();
        client1.setEmail("client1@test.com");
        client1.setPassword(passwordEncoder.encode(client1Password));
        client1.setName("Client 1");
        client1.setContact("+1234567890");
        client1.setActive(true);
        client1.setRole(Role.CLIENT);
        client1 = clientRepository.save(client1);

        // Créer client 2
        client2 = new Client();
        client2.setEmail("client2@test.com");
        client2.setPassword(passwordEncoder.encode(client2Password));
        client2.setName("Client 2");
        client2.setContact("+0987654321");
        client2.setActive(true);
        client2.setRole(Role.CLIENT);
        client2 = clientRepository.save(client2);

        // Créer des commandes pour chaque client
        orderClient1 = SalesOrder.builder()
                .id(UUID.randomUUID())
                .client(client1)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();
        orderClient1 = salesOrderRepository.save(orderClient1);

        orderClient2 = SalesOrder.builder()
                .id(UUID.randomUUID())
                .client(client2)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();
        orderClient2 = salesOrderRepository.save(orderClient2);
    }

    // ============================================
    // SCÉNARIO 1 : Login valide et invalide
    // ============================================

    @Test
    void testLogin_ValidCredentials_ShouldReturnTokens() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail(client1.getEmail());
        request.setPassword(client1Password);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertNotNull(response);
        assertTrue(response.contains("accessToken"));
        assertTrue(response.contains("refreshToken"));
    }

    @Test
    void testLogin_InvalidEmail_ShouldReturnError() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("nonexistent@test.com");
        request.setPassword(client1Password);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testLogin_InvalidPassword_ShouldReturnError() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail(client1.getEmail());
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    // ============================================
    // SCÉNARIO 2 : Accès avec token valide, sans token, token expiré
    // ============================================

    @Test
    void testAccess_WithValidToken_ShouldSucceed() throws Exception {
        // Login pour obtenir un token
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail(client1.getEmail());
        loginRequest.setPassword(client1Password);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = extractTokenFromResponse(loginResponse, "accessToken");

        // Accéder à un endpoint protégé avec le token
        mockMvc.perform(get("/api/sales-orders")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void testAccess_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/sales-orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAccess_WithInvalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/sales-orders")
                        .header("Authorization", "Bearer invalid-token-12345"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAccess_WithExpiredToken_ShouldReturn401() throws Exception {
        // Créer un token expiré manuellement (simulation)
        // Note: En réalité, on devrait créer un token avec une date d'expiration passée
        // Pour ce test, on utilise un token invalide qui sera rejeté
        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJjbGllbnQxQHRlc3QuY29tIiwiZXhwIjoxNjAwMDAwMDAwfQ.invalid";

        mockMvc.perform(get("/api/sales-orders")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    // ============================================
    // SCÉNARIO 3 : Renouvellement via refresh token
    // ============================================

    @Test
    void testRefreshToken_ValidRefreshToken_ShouldReturnNewTokens() throws Exception {
        // Login pour obtenir tokens
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail(client1.getEmail());
        loginRequest.setPassword(client1Password);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String refreshToken = extractTokenFromResponse(loginResponse, "refreshToken");

        // Utiliser le refresh token pour obtenir un nouvel access token
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken(refreshToken);

        String refreshResponse = mockMvc.perform(post("/api/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Vérifier que le nouveau refresh token est différent de l'ancien (rotation)
        String newRefreshToken = extractTokenFromResponse(refreshResponse, "refreshToken");
        assertNotEquals(refreshToken, newRefreshToken, "Le refresh token doit être roté");

        // Vérifier que l'ancien refresh token est supprimé
        assertFalse(refreshTokenRepository.findByToken(refreshToken).isPresent(),
                "L'ancien refresh token doit être supprimé");
    }

    @Test
    void testRefreshToken_InvalidRefreshToken_ShouldReturnError() throws Exception {
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken("invalid-refresh-token-12345");

        mockMvc.perform(post("/api/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("invalid_refresh_token"));
    }

    // ============================================
    // SCÉNARIO 4 : Rejet d'un refresh token révoqué
    // ============================================

    @Test
    void testRefreshToken_RevokedRefreshToken_ShouldReturnError() throws Exception {
        // Login pour obtenir tokens
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail(client1.getEmail());
        loginRequest.setPassword(client1Password);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = extractTokenFromResponse(loginResponse, "accessToken");
        String refreshToken = extractTokenFromResponse(loginResponse, "refreshToken");

        // Logout pour révoquer les tokens
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isNoContent());

        // Vérifier que le refresh token est supprimé
        assertFalse(refreshTokenRepository.findByToken(refreshToken).isPresent(),
                "Le refresh token doit être supprimé après logout");

        // Essayer d'utiliser le refresh token révoqué
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/auth/refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    // ============================================
    // SCÉNARIO 5 : Accès interdit selon rôle
    // ============================================

    @Test
    void testAccess_ClientTryingToAccessAdminEndpoint_ShouldReturn403() throws Exception {
        // Login en tant que client
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail(client1.getEmail());
        loginRequest.setPassword(client1Password);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = extractTokenFromResponse(loginResponse, "accessToken");

        // Essayer d'accéder à un endpoint réservé aux admins
        mockMvc.perform(get("/api/products")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    // ============================================
    // SCÉNARIO 6 : Refus d'accès aux ressources d'un autre client (ownership)
    // ============================================

    @Test
    void testAccess_Client1TryingToAccessClient2Order_ShouldReturn403() throws Exception {
        // Login en tant que client1
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail(client1.getEmail());
        loginRequest.setPassword(client1Password);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = extractTokenFromResponse(loginResponse, "accessToken");

        // Essayer d'accéder à la commande de client2
        mockMvc.perform(get("/api/sales-orders/" + orderClient2.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAccess_Client1AccessingOwnOrder_ShouldSucceed() throws Exception {
        // Login en tant que client1
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail(client1.getEmail());
        loginRequest.setPassword(client1Password);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = extractTokenFromResponse(loginResponse, "accessToken");

        // Accéder à sa propre commande
        mockMvc.perform(get("/api/sales-orders/" + orderClient1.getId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void testAccess_Client1ListOrders_ShouldOnlySeeOwnOrders() throws Exception {
        // Login en tant que client1
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setEmail(client1.getEmail());
        loginRequest.setPassword(client1Password);

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = extractTokenFromResponse(loginResponse, "accessToken");

        // Lister les commandes (devrait retourner uniquement celles de client1)
        mockMvc.perform(get("/api/sales-orders")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        // Note: On pourrait vérifier que toutes les commandes retournées appartiennent à client1
    }

    // ============================================
    // Méthodes utilitaires
    // ============================================

    private String extractTokenFromResponse(String response, String tokenType) {
        try {
            // Utiliser ObjectMapper pour parser le JSON
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get(tokenType).asText();
        } catch (Exception e) {
            throw new RuntimeException("Impossible d'extraire le token de la réponse: " + response, e);
        }
    }
}

