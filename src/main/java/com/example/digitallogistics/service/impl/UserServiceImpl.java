package com.example.digitallogistics.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.repository.UserRepository;
import com.example.digitallogistics.service.UserService;
import com.example.digitallogistics.model.enums.Role;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

        @Override
        public List<User> findByRole(Role role) {
            return userRepository.findByRole(role);
        }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> update(UUID id, User user) {
        return userRepository.findById(id).map(existing -> {
            existing.setEmail(user.getEmail());
            existing.setPassword(user.getPassword());
            existing.setRole(user.getRole());
            existing.setActive(user.isActive());
            return userRepository.save(existing);
        });
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }
}
