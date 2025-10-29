package com.example.digitallogistics.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import com.example.digitallogistics.model.dto.UserCreateDto;
import com.example.digitallogistics.model.dto.UserDto;
import com.example.digitallogistics.model.dto.UserUpdateDto;
import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.mapper.UserMapper;
import com.example.digitallogistics.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<UserDto> listAll() {
        return userService.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable UUID id) {
        return userService.findById(id).map(userMapper::toDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserDto> getByEmail(@RequestParam String email) {
        Optional<User> u = userService.findByEmail(email);
        return u.map(userMapper::toDto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/role/{role}")
    public List<UserDto> getByRole(@PathVariable String role) {
        try {
            com.example.digitallogistics.model.enums.Role r = com.example.digitallogistics.model.enums.Role.valueOf(role.toUpperCase());
            return userService.findByRole(r).stream().map(userMapper::toDto).collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            return java.util.Collections.emptyList();
        }
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserCreateDto dto) {
        User user = userMapper.toEntity(dto);
        if (user.getId() == null) {
            user.setId(java.util.UUID.randomUUID());
        }
        User created = userService.create(user);
        UserDto out = userMapper.toDto(created);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId())).body(out);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable UUID id, @RequestBody UserUpdateDto dto) {
        Optional<User> existing = userService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = existing.get();
        userMapper.updateFromDto(dto, user);
        // save updated entity
        User saved = userService.create(user);
        return ResponseEntity.ok(userMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
