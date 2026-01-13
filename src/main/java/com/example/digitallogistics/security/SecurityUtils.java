package com.example.digitallogistics.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.digitallogistics.model.entity.Client;
import com.example.digitallogistics.model.entity.User;
import com.example.digitallogistics.model.enums.Role;
import com.example.digitallogistics.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {

    /**
     * Récupère l'email de l'utilisateur actuellement authentifié
     */
    public static Optional<String> getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            return Optional.of(((UserDetails) principal).getUsername());
        }

        if (principal instanceof org.springframework.security.oauth2.jwt.Jwt) {
            org.springframework.security.oauth2.jwt.Jwt jwt = (org.springframework.security.oauth2.jwt.Jwt) principal;
            String email = null;
            if (jwt.hasClaim("email")) {
                email = jwt.getClaimAsString("email");
            } else if (jwt.hasClaim("preferred_username")) {
                email = jwt.getClaimAsString("preferred_username");
            } else {
                email = jwt.getSubject();
            }
            System.out.println("SecurityUtils: JWT Principal. Email/Sub: " + email);
            return Optional.ofNullable(email);
        }

        System.out.println("SecurityUtils: Principal is " + principal.getClass().getName());
        return Optional.empty();
    }

    /**
     * Récupère l'utilisateur actuellement authentifié
     */
    public static Optional<User> getCurrentUser(UserRepository userRepository) {
        return getCurrentUserEmail()
                .flatMap(userRepository::findByEmail);
    }

    /**
     * Vérifie si l'utilisateur actuel est un client
     */
    public static boolean isClient(UserRepository userRepository) {
        return getCurrentUser(userRepository)
                .map(user -> user.getRole() == Role.CLIENT)
                .orElse(false);
    }

    /**
     * Vérifie si l'utilisateur actuel est un client et récupère son ID
     */
    public static Optional<UUID> getCurrentClientId(UserRepository userRepository) {
        return getCurrentUser(userRepository)
                .filter(user -> user instanceof Client)
                .filter(user -> user.getRole() == Role.CLIENT)
                .map(User::getId);
    }

    /**
     * Vérifie si l'utilisateur actuel a le rôle ADMIN ou WAREHOUSE_MANAGER
     */
    public static boolean isAdminOrManager(UserRepository userRepository) {
        return getCurrentUser(userRepository)
                .map(user -> user.getRole() == Role.ADMIN || user.getRole() == Role.WAREHOUSE_MANAGER)
                .orElse(false);
    }
}
