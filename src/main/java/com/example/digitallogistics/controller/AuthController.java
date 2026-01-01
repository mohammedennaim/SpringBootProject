package com.example.digitallogistics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.digitallogistics.service.UserService;
import com.example.digitallogistics.service.RefreshTokenService;
import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.entity.RefreshToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.AuthRequest;
import com.example.digitallogistics.model.dto.AuthResponse;
import com.example.digitallogistics.model.dto.TokenRefreshRequest;
import com.example.digitallogistics.model.dto.TokenRefreshResponse;
import com.example.digitallogistics.model.dto.UserCreateDto;
import com.example.digitallogistics.util.JwtTokenProvider;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API d'authentification (login, register, logout, refresh token)")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
                          PasswordEncoder passwordEncoder, UserService userService,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion", description = "Authentifie un utilisateur et retourne un access token et un refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie"),
        @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
        @ApiResponse(responseCode = "500", description = "Erreur interne")
    })
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            String accessToken = tokenProvider.createToken(auth.getName(), auth.getAuthorities());

            Optional<User> userOpt = userService.findByEmail(auth.getName());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(500).body(Map.of(
                    "error", "user_not_found",
                    "message", "Utilisateur introuvable pour l'email fourni"
                ));
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userOpt.get());

            return ResponseEntity.ok(
                AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .build()
            );
        } catch (Exception ex) {
            Throwable root = ex;
            while (root.getCause() != null) root = root.getCause();
            String message = root.getMessage() != null ? root.getMessage() : ex.getMessage();
            return ResponseEntity.status(500).body(Map.of(
                "error", "internal_error",
                "message", message
            ));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription", description = "Crée un nouveau compte client")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscription réussie"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<?> register(@RequestBody @Valid UserCreateDto createDto) {
    Client client = new Client();
    client.setEmail(createDto.getEmail());
    client.setPassword(passwordEncoder.encode(createDto.getPassword()));
    client.setActive(true);
    client.setName(createDto.getName());
    client.setContact(createDto.getContact());

        Client saved = (Client) userService.create(client);

    return ResponseEntity.ok(Map.of(
        "id", saved.getId().toString(),
        "name", saved.getName(),
        "email", saved.getEmail(),
        "contact", saved.getContact(),
        "role", saved.getRole().name(),
        "active", saved.isActive()
        
        
    ));
    }

    @PostMapping("/logout")
    @Operation(summary = "Déconnexion", description = "Révoque l'access token et le refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Déconnexion réussie"),
        @ApiResponse(responseCode = "400", description = "Token manquant")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request, @RequestBody(required = false) Map<String, String> body) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = null;
        String refreshToken = null;
        
        if (header != null && header.startsWith("Bearer ")) {
            accessToken = header.substring(7);
        }
        if (body != null) {
            if (accessToken == null) {
                accessToken = body.get("token");
            }
            refreshToken = body.get("refreshToken");
        }

        if (accessToken == null || accessToken.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        // Révoquer l'access token
        tokenProvider.revokeToken(accessToken);
        
        // Révoquer le refresh token si fourni
        if (refreshToken != null && !refreshToken.isBlank()) {
            final String finalRefreshToken = refreshToken; // Variable finale pour la lambda
            refreshTokenService.findByToken(refreshToken)
                .ifPresent(token -> refreshTokenService.deleteByToken(finalRefreshToken));
        } else {
            // Si le refresh token n'est pas fourni, essayer de le trouver via l'utilisateur
            try {
                String email = tokenProvider.getSubject(accessToken);
                userService.findByEmail(email)
                    .ifPresent(user -> refreshTokenService.deleteByUser(user));
            } catch (Exception ex) {
                // Ignorer si le token est invalide ou expiré
            }
        }
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refreshtoken")
    @Operation(summary = "Rafraîchir le token", description = "Génère un nouvel access token et un nouveau refresh token à partir d'un refresh token valide")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tokens rafraîchis avec succès"),
        @ApiResponse(responseCode = "401", description = "Refresh token invalide ou expiré")
    })
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        Optional<RefreshToken> refreshTokenOpt = refreshTokenService.findByToken(requestRefreshToken);
        if (refreshTokenOpt.isEmpty()) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "error", "invalid_refresh_token",
                        "message", "Refresh token is not in database!",
                        "timestamp", System.currentTimeMillis()
                    ));
        }

        try {
            RefreshToken refreshToken = refreshTokenService.verifyExpiration(refreshTokenOpt.get());
            User user = refreshToken.getUser();
            
            // Rotation du refresh token : créer un nouveau et supprimer l'ancien
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
            refreshTokenService.deleteByToken(requestRefreshToken);
            
            // Générer un nouvel access token avec les rôles
            java.util.List<SimpleGrantedAuthority> authorities = java.util.Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
            String accessToken = tokenProvider.createToken(user.getEmail(), authorities);
            
            TokenRefreshResponse response = TokenRefreshResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(newRefreshToken.getToken())
                    .build();
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            // Token expiré
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "error", "expired_refresh_token",
                        "message", ex.getMessage(),
                        "timestamp", System.currentTimeMillis()
                    ));
        }
    }
}
