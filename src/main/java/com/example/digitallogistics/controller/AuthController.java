package com.example.digitallogistics.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.dto.AuthRequest;
import com.example.digitallogistics.dto.AuthResponse;
import com.example.digitallogistics.model.dto.UserCreateDto;
import com.example.digitallogistics.model.dto.UserDto;
import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.mapper.UserMapper;
import com.example.digitallogistics.service.UserService;
import com.example.digitallogistics.util.JwtTokenProvider;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
                          UserService userService, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest req) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        String token = tokenProvider.createToken(auth.getName());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid UserCreateDto createDto) {
        // encode password
        createDto.setPassword(passwordEncoder.encode(createDto.getPassword()));
        User user = userMapper.toEntity(createDto);
        User saved = userService.create(user);
        UserDto dto = userMapper.toDto(saved);
        return ResponseEntity.ok(dto);
    }
}
