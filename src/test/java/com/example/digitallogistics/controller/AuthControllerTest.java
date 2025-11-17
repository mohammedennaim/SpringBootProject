package com.example.digitallogistics.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.example.digitallogistics.model.dto.AuthRequest;
import com.example.digitallogistics.model.dto.UserCreateDto;
import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.service.UserService;
import com.example.digitallogistics.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserService userService;

    @Test
    void login_shouldReturnToken() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        Authentication auth = new UsernamePasswordAuthenticationToken(
            "test@example.com", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_CLIENT"))
        );

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(tokenProvider.createToken(anyString(), any())).thenReturn("test-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"));
    }

    @Test
    void register_shouldCreateClient() throws Exception {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setEmail("new@example.com");
        createDto.setPassword("password");
        createDto.setName("New User");
        createDto.setContact("+1234567890");
        createDto.setRole("CLIENT");

        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setEmail("new@example.com");
        client.setName("New User");
        client.setContact("+1234567890");
        client.setActive(true);
        client.setRole(Role.CLIENT);

        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userService.create(any())).thenReturn(client);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.name").value("New User"));
    }

    @Test
    void logout_shouldRevokeToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isNoContent());
    }

    @Test
    void logout_withBodyToken_shouldRevokeToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"test-token\"}"))
                .andExpect(status().isNoContent());
    }
}
