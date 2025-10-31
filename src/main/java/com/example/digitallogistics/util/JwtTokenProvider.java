package com.example.digitallogistics.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long validityInMilliseconds;
    private final Map<String, Date> revokedTokens = new ConcurrentHashMap<>();

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

    /**
     * Create a token including a 'roles' claim (comma-separated) for convenience.
     */
    public String createToken(String subject, Collection<? extends GrantedAuthority> authorities) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + validityInMilliseconds);

    String roles = authorities == null ? "" : authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    return Jwts.builder()
        .setSubject(subject)
        .claim("roles", roles)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
    }

    public boolean validateToken(String token) {
        try {
            // cleanup expired revoked tokens opportunistically
            Date now = new Date();
            revokedTokens.entrySet().removeIf(e -> e.getValue().before(now));

            // check revoked first
            if (revokedTokens.containsKey(token)) {
                return false;
            }

            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(now);
        } catch (Exception ex) {
            return false;
        }
    }

    public void revokeToken(String token) {
        try {
            Date expiry = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration();
            revokedTokens.put(token, expiry == null ? new Date() : expiry);
        } catch (Exception ex) {
            // if token invalid/unparsable, still store a short-lived block to avoid reuse; set expiry to now
            revokedTokens.put(token, new Date());
        }
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
}
