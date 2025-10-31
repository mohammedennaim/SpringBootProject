package com.example.digitallogistics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.dto.AuthRequest;
import com.example.digitallogistics.dto.AuthResponse;
import com.example.digitallogistics.model.dto.UserCreateDto;
import com.example.digitallogistics.util.JwtTokenProvider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
                          PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            String token = tokenProvider.createToken(auth.getName(), auth.getAuthorities());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (Exception ex) {
            // unwrap to the root cause to provide the underlying SQL error when present
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
        // Direct JDBC insert to avoid JPA loading of joined inheritance (workaround for schema mismatch)
        java.util.UUID id = java.util.UUID.randomUUID();
    String encoded = passwordEncoder.encode(createDto.getPassword());
    String role = "CLIENT";
    boolean active = true;

    // include the discriminator column `user_type` to satisfy the NOT NULL constraint
    jdbcTemplate.update(
        "INSERT INTO users(id, email, password, role, active, user_type) VALUES (?::uuid, ?, ?, ?, ?, ?)",
        id.toString(), createDto.getEmail(), encoded, role, active, role
    );

        return ResponseEntity.ok(Map.of(
                "id", id.toString(),
                "email", createDto.getEmail(),
                "role", role,
                "active", active
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, @RequestBody(required = false) Map<String, String> body) {
        // Try Authorization header first
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }
        // fallback to request body { "token": "..." }
        if (token == null && body != null) {
            token = body.get("token");
        }

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        tokenProvider.revokeToken(token);
        return ResponseEntity.noContent().build();
    }
}
