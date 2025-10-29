package com.example.digitallogistics.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long validityInMilliseconds;

    // HS256 requires a 256-bit (32-byte) key. If the configured secret is shorter, pad it deterministically.
    public JwtTokenProvider(@Value("${app.jwt.secret:defaultSecretKeyChangeMe}") String secret,
                            @Value("${app.jwt.expiration-ms:3600000}") long validityInMilliseconds) {
        byte[] bytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            byte[] padded = new byte[32];
            for (int i = 0; i < 32; i++) {
                padded[i] = bytes.length == 0 ? 0 : bytes[i % bytes.length];
            }
            bytes = padded;
        } else if (bytes.length > 32) {
            bytes = Arrays.copyOf(bytes, 32);
        }

        this.key = Keys.hmacShaKeyFor(bytes);
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String createToken(String subject) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
}
