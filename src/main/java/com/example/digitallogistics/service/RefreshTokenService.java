package com.example.digitallogistics.service;

import java.util.Optional;

import com.example.digitallogistics.model.entity.RefreshToken;
import com.example.digitallogistics.model.entity.User;

public interface RefreshTokenService {

    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(RefreshToken token);

    int deleteByUser(User user);
}


