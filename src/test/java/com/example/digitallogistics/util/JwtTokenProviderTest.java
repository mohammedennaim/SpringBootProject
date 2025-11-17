package com.example.digitallogistics.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider("mySecretKeyForTestingPurposes123", 3600000);
    }

    @Test
    void createToken_WithSubject_ShouldReturnValidToken() {
        String subject = "testuser@example.com";
        
        String token = jwtTokenProvider.createToken(subject);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(subject, jwtTokenProvider.getSubject(token));
    }

    @Test
    void createToken_WithSubjectAndAuthorities_ShouldReturnValidToken() {
        String subject = "testuser@example.com";
        Collection<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        
        String token = jwtTokenProvider.createToken(subject, authorities);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(subject, jwtTokenProvider.getSubject(token));
    }

    @Test
    void createToken_WithNullAuthorities_ShouldReturnValidToken() {
        String subject = "testuser@example.com";
        
        String token = jwtTokenProvider.createToken(subject, null);
        
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(subject, jwtTokenProvider.getSubject(token));
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        String token = jwtTokenProvider.createToken("testuser@example.com");
        
        boolean isValid = jwtTokenProvider.validateToken(token);
        
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid.token.here";
        
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);
        
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithRevokedToken_ShouldReturnFalse() {
        String token = jwtTokenProvider.createToken("testuser@example.com");
        
        jwtTokenProvider.revokeToken(token);
        boolean isValid = jwtTokenProvider.validateToken(token);
        
        assertFalse(isValid);
    }

    @Test
    void revokeToken_WithValidToken_ShouldRevokeSuccessfully() {
        String token = jwtTokenProvider.createToken("testuser@example.com");
        assertTrue(jwtTokenProvider.validateToken(token));
        
        jwtTokenProvider.revokeToken(token);
        
        assertFalse(jwtTokenProvider.validateToken(token));
    }

    @Test
    void revokeToken_WithInvalidToken_ShouldHandleGracefully() {
        String invalidToken = "invalid.token.here";
        
        assertDoesNotThrow(() -> jwtTokenProvider.revokeToken(invalidToken));
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    void getSubject_WithValidToken_ShouldReturnSubject() {
        String expectedSubject = "testuser@example.com";
        String token = jwtTokenProvider.createToken(expectedSubject);
        
        String actualSubject = jwtTokenProvider.getSubject(token);
        
        assertEquals(expectedSubject, actualSubject);
    }

    @Test
    void constructor_WithShortSecret_ShouldPadSecret() {
        JwtTokenProvider provider = new JwtTokenProvider("short", 3600000);
        String token = provider.createToken("test@example.com");
        
        assertNotNull(token);
        assertTrue(provider.validateToken(token));
    }

    @Test
    void constructor_WithLongSecret_ShouldTruncateSecret() {
        String longSecret = "thisIsAVeryLongSecretKeyThatExceedsThirtyTwoCharactersInLength";
        JwtTokenProvider provider = new JwtTokenProvider(longSecret, 3600000);
        String token = provider.createToken("test@example.com");
        
        assertNotNull(token);
        assertTrue(provider.validateToken(token));
    }

    @Test
    void constructor_WithNullSecret_ShouldUseDefault() {
        JwtTokenProvider provider = new JwtTokenProvider(null, 3600000);
        String token = provider.createToken("test@example.com");
        
        assertNotNull(token);
        assertTrue(provider.validateToken(token));
    }
}