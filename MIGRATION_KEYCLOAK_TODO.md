# TODO : Migration de la Sécurité Custom JWT vers Keycloak OAuth2

Ce document liste toutes les étapes nécessaires pour migrer l'application LogisticsFlow d'une authentification JWT custom vers Keycloak OAuth2 Resource Server.

## 📊 État actuel vs État cible

### ✅ Actuellement implémenté (JWT Custom)
- ✅ `JwtTokenProvider` - Génération et validation de tokens custom
- ✅ `JwtAuthenticationFilter` - Filtre Spring Security custom
- ✅ `AuthController` - Login/Register avec tokens custom
- ✅ `RefreshToken` - Entity et service pour refresh tokens
- ✅ `UserDetailsServiceImpl` - Chargement des utilisateurs depuis PostgreSQL
- ✅ Utilisateurs stockés en base de données locale

### 🎯 État cible (Keycloak OAuth2)
- 🎯 Keycloak comme Identity Provider (IdP) unique
- 🎯 OAuth2 Resource Server pour validation des JWT Keycloak
- 🎯 Rôles métier gérés dans Keycloak
- 🎯 Sessions et logout gérés par Keycloak
- 🎯 Tokens signés et émis par Keycloak
- 🎯 Audit et traçabilité dans Keycloak

---

## 📋 ROADMAP DE MIGRATION

### Phase 1 : Configuration Keycloak (✅ TERMINÉ)

#### ✅ 1.1 Infrastructure Keycloak
- [x] Service Keycloak dans docker-compose.yaml
- [x] Realm `logistics-realm` créé
- [x] Import/Export automatique configuré
- [x] Documentation KEYCLOAK_SETUP.md

#### ✅ 1.2 Rôles et Utilisateurs
- [x] Rôles métier : ADMIN, WAREHOUSE_MANAGER, CLIENT
- [x] 5 utilisateurs de test configurés
- [x] Mapping utilisateurs avec rôles

#### ✅ 1.3 Clients OIDC
- [x] Client `logistics-backend` (Confidential)
- [x] Client `logistics-frontend` (Public)
- [x] Protocol Mappers pour inclure les rôles dans les JWT

---

### Phase 2 : Préparation Spring Boot (🔴 À FAIRE)

#### 🔴 2.1 Créer le JWT Converter pour Keycloak

**Fichier à créer** : `src/main/java/com/example/digitallogistics/security/KeycloakJwtConverter.java`

**Objectif** : Extraire les rôles depuis les JWT Keycloak et les convertir en authorities Spring Security.

**Contenu attendu** :
```java
package com.example.digitallogistics.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Convertit les JWT Keycloak en Authentication Spring Security.
 * Extrait les rôles depuis le claim "roles" et les préfixe avec "ROLE_".
 */
@Component
public class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Extraire les rôles depuis le claim "roles"
        List<String> roles = jwt.getClaimAsStringList("roles");
        
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        
        // Convertir les rôles en authorities avec le préfixe ROLE_
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
```

**Bonnes pratiques** :
- ✅ Utilise `@Component` pour l'injection automatique
- ✅ Préfixe `ROLE_` pour compatibilité avec `@PreAuthorize("hasRole('ADMIN')")`
- ✅ Gère les cas où le claim "roles" est absent ou vide
- ✅ Utilise `JwtAuthenticationToken` standard de Spring Security

**Test à créer** : `KeycloakJwtConverterTest.java`

---

#### 🔴 2.2 Modifier SecurityConfig pour OAuth2 Resource Server

**Fichier à modifier** : `src/main/java/com/example/digitallogistics/security/SecurityConfig.java`

**Changements requis** :

1. **Supprimer l'injection du custom filter** :
```java
// AVANT (À SUPPRIMER)
private final JwtAuthenticationFilter jwtAuthenticationFilter;

public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
}
```

2. **Injecter le KeycloakJwtConverter** :
```java
// APRÈS (À AJOUTER)
private final KeycloakJwtConverter keycloakJwtConverter;

public SecurityConfig(KeycloakJwtConverter keycloakJwtConverter) {
    this.keycloakJwtConverter = keycloakJwtConverter;
}
```

3. **Remplacer le filtre custom par OAuth2 Resource Server** :
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/github-webhook/**").permitAll()
            .requestMatchers("/api/auth/**").permitAll() // Garder pour login initial
            .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().authenticated()
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter))
        )
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint())
        );

    // SUPPRIMER cette ligne :
    // .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

**Bonnes pratiques** :
- ✅ Utilise `.oauth2ResourceServer()` au lieu d'un filtre custom
- ✅ Configure le converter pour extraire les rôles correctement
- ✅ Garde les endpoints publics (`/api/auth/**`, `/swagger-ui/**`)
- ✅ Maintient la politique de session STATELESS

**Test à vérifier** : Les tests `SecurityConfigTest` doivent être mis à jour.

---

## 📚 Bonnes Pratiques et Recommandations

### ✅ Sécurité

1. **Ne JAMAIS commiter les secrets Keycloak**
   - Utiliser des variables d'environnement
   - Fichier `.env` dans `.gitignore`

2. **Utiliser HTTPS en production**
   - Configurer SSL/TLS sur Keycloak
   - `sslRequired: "all"` dans le realm

3. **Rotation des secrets**
   - Changer régulièrement le client secret
   - Documenter la procédure

4. **Principe du moindre privilège**
   - Attribuer uniquement les rôles nécessaires
   - Utiliser les groupes pour simplifier la gestion

5. **Monitoring et Alertes**
   - Surveiller les tentatives de connexion échouées
   - Alerter sur les activités suspectes
   - Exporter les logs Keycloak vers un SIEM

---

## 📊 Checklist de Migration

### Préparation
- [x] Keycloak démarré et opérationnel
- [x] Realm `logistics-realm` importé avec succès
- [x] Client secret récupéré et stocké en sécurité
- [x] Utilisateurs de test créés et vérifiés

### Développement
- [ ] `KeycloakJwtConverter.java` créé et testé
- [ ] `SecurityConfig.java` modifié pour OAuth2
- [ ] `AuthController.java` adapté ou remplacé
- [ ] `User.java` nettoyé (suppression du password)
- [ ] Tests d'intégration créés et passants

### Configuration
- [ ] Groupes Keycloak créés
- [ ] Audit et événements activés
- [ ] Politiques de mots de passe configurées
- [ ] Protection brute force activée
- [ ] Durées de vie des tokens ajustées

---

**Date de création** : 30 Décembre 2025  
**Auteur** : Mohamed Hmidouch  
**Version** : 1.0  
**Statut** : 🔴 TODO - Migration non commencée

---

## Notes Finales

Cette migration est une opportunité d'améliorer significativement la sécurité de l'application en :
- Centralisant la gestion des identités
- Utilisant des standards OAuth2/OIDC reconnus
- Déléguant la sécurité à un composant dédié et maintenu
- Facilitant l'audit et la conformité

Prendre le temps de bien faire cette migration permettra d'avoir une architecture plus robuste et maintenable à long terme.

**Bon courage pour la migration ! 🚀**
