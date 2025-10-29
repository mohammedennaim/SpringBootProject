package com.example.digitallogistics.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.enums.Role;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    User create(User user);
    Optional<User> update(UUID id, User user);
    void delete(UUID id);
}
