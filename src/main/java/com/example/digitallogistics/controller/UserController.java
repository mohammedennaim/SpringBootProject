package com.example.digitallogistics.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.digitallogistics.model.dto.UserCreateDto;
import com.example.digitallogistics.model.dto.UserDto;
import com.example.digitallogistics.model.dto.UserUpdateDto;
import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.model.mapper.UserMapper;
import com.example.digitallogistics.service.UserService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // ADMIN only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> list() {
        return userService.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    // Public registration endpoint (same behavior as /api/auth/register)
    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody @Valid UserCreateDto createDto) {
        // force CLIENT role for self-registration
        createDto.setRole(Role.CLIENT);
        // encode password
        createDto.setPassword(passwordEncoder.encode(createDto.getPassword()));
        User u = userMapper.toEntity(createDto);
        u.setRole(Role.CLIENT);
        User saved = userService.create(u);
        UserDto dto = userMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/users/" + dto.getId())).body(dto);
    }

    // Details: allowed for ADMIN or the user themself
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable UUID id, Authentication auth) {
        Optional<User> opt = userService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        User u = opt.get();
        if (isAdmin(auth) || (auth != null && auth.getName().equals(u.getEmail()))) {
            return ResponseEntity.ok(userMapper.toDto(u));
        }
        return ResponseEntity.status(403).build();
    }

    // Update profile: ADMIN can update everything; user can update own email/password
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable UUID id, @RequestBody UserUpdateDto dto, Authentication auth) {
        Optional<User> opt = userService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        User existing = opt.get();

    boolean admin = isAdmin(auth);
    boolean self = auth != null && auth.getName().equals(existing.getEmail());

    if (!admin && !self) return ResponseEntity.status(403).build();

        // Non-admins cannot change role or active flag
        if (!admin) {
            dto.setRole((Role)null);
            dto.setActive(null);
        }

        // If password provided, encode it
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userMapper.updateFromDto(dto, existing);
        User saved = userService.update(id, existing).orElse(existing);
        return ResponseEntity.ok(userMapper.toDto(saved));
    }

    // Deactivate account: ADMIN or self
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id, Authentication auth) {
        Optional<User> opt = userService.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        User existing = opt.get();

        if (!isAdmin(auth) && !(auth != null && auth.getName().equals(existing.getEmail()))) {
            return ResponseEntity.status(403).build();
        }

        existing.setActive(false);
        userService.update(id, existing);
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(Authentication auth) {
        if (auth == null) return false;
        for (GrantedAuthority g : auth.getAuthorities()) {
            if ("ROLE_ADMIN".equals(g.getAuthority())) return true;
        }
        return false;
    }
}
