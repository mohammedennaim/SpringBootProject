package com.example.digitallogistics.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.digitallogistics.model.entity.RefreshToken;
import com.example.digitallogistics.model.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    int deleteByUser(User user);
    
    void deleteByToken(String token);
}


