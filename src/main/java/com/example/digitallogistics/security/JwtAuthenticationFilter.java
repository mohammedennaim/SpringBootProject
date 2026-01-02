package com.example.digitallogistics.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// @Component - Désactivé car nous utilisons maintenant OAuth2 Resource Server avec Keycloak
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.digitallogistics.util.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// @Component - Désactivé: Ce filtre n'est plus utilisé car nous utilisons OAuth2 Resource Server
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (tokenProvider.validateToken(token)) {
                    String username = tokenProvider.getSubject(token);
                    try {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } catch (UsernameNotFoundException ex) {
                        // Utilisateur non trouvé - ne pas définir d'authentification, Spring Security appellera l'entry point
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    // Token invalide - ne pas définir d'authentification, Spring Security appellera l'entry point
                    SecurityContextHolder.clearContext();
                }
            } catch (Exception ex) {
                // Erreur lors de la validation - ne pas définir d'authentification, Spring Security appellera l'entry point
                SecurityContextHolder.clearContext();
            }
        }
        // Si pas de header Authorization, on laisse passer pour que AuthenticationEntryPoint soit appelé par Spring Security

        filterChain.doFilter(request, response);
    }
}
