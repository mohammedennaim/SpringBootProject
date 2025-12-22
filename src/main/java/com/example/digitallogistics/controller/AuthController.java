package com.example.digitallogistics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

@RestController
@RequestMapping("/api/auth")
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
    public ResponseEntity<Void> logout(HttpServletRequest request, @RequestBody(required = false) Map<String, String> body) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }
        if (token == null && body != null) {
            token = body.get("token");
        }

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        tokenProvider.revokeToken(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = tokenProvider.generateTokenFromUsername(user.getEmail());
                    TokenRefreshResponse response = TokenRefreshResponse.builder()
                            .accessToken(token)
                            .refreshToken(requestRefreshToken)
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }
}
