package com.example.digitallogistics.security;

import java.util.Collections;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public CustomUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Map<String, Object> row = jdbcTemplate.queryForMap(
                    "SELECT email, password, role, active FROM users WHERE email = ?",
                    username);

            String email = (String) row.get("email");
            String password = (String) row.get("password");
            Object activeObj = row.get("active");
            boolean active = activeObj instanceof Boolean ? (Boolean) activeObj : Boolean.parseBoolean(String.valueOf(activeObj));
            String role = (String) row.get("role");

            return new org.springframework.security.core.userdetails.User(
                    email,
                    password,
                    active, true, true, true,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
            );
        } catch (EmptyResultDataAccessException ex) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
