# Guide de Configuration Keycloak - LogisticsFlow

Ce guide explique comment utiliser et maintenir la configuration Keycloak pour le projet LogisticsFlow avec import/export automatique.

## 📋 Table des matières

1. [Architecture](#architecture)
2. [Configuration actuelle](#configuration-actuelle)
3. [Démarrage rapide](#démarrage-rapide)
4. [Workflow de collaboration](#workflow-de-collaboration)
5. [Exporter la configuration Keycloak](#exporter-la-configuration-keycloak)
6. [Modifier la configuration](#modifier-la-configuration)
7. [Utilisateurs et rôles](#utilisateurs-et-rôles)
8. [Clients configurés](#clients-configurés)
9. [Troubleshooting](#troubleshooting)

---

## 🏗️ Architecture

Le projet utilise **Keycloak 24.0** comme serveur d'authentification et d'autorisation. La configuration est stockée dans un fichier JSON qui est automatiquement importé au démarrage du conteneur Keycloak.

### Structure des fichiers
```
logisticsFlow/
├── keycloak-config/
│   └── realm-export.json       # Configuration complète du realm
├── docker-compose.yaml          # Service Keycloak avec auto-import
└── KEYCLOAK_SETUP.md           # Ce guide
```

---

## ⚙️ Configuration actuelle

### Realm
- **Nom du realm** : `logistics-realm`
- **URL d'accès** : `http://localhost:8080/auth/realms/logistics-realm`
- **Console Admin** : `http://localhost:8080/auth/admin`

### Credentials Admin
- **Username** : `admin`
- **Password** : `admin`

### Token Configuration
- **Access Token Lifespan** : 3600 secondes (1 heure)
- **SSO Session Idle Timeout** : 1800 secondes (30 minutes)
- **SSO Session Max Lifespan** : 36000 secondes (10 heures)

---

## 🚀 Démarrage rapide

### 1. Premier démarrage

```bash
# Cloner le projet
git clone <repo-url>
cd logisticsFlow

# Vérifier que le fichier de configuration existe
ls -l keycloak-config/realm-export.json

# Démarrer tous les services (Keycloak + PostgreSQL + autres)
docker compose up -d

# Vérifier que Keycloak a bien importé le realm
docker logs keycloak
```

### 2. Accéder à Keycloak

1. Ouvrir : `http://localhost:8080/auth`
2. Cliquer sur **Administration Console**
3. Se connecter avec `admin` / `admin`
4. Vérifier que le realm **logistics-realm** est présent dans le menu déroulant en haut à gauche

### 3. Tester l'authentification

Le realm est pré-configuré avec des utilisateurs de test :

| Email | Mot de passe | Rôle |
|-------|--------------|------|
| admin1@system.com | adminpass | ADMIN |
| manager1@system.com | managerpass | WAREHOUSE_MANAGER |
| client1@system.com | pass123 | CLIENT |
| client2@system.com | pass456 | CLIENT |
| client3@system.com | pass789 | CLIENT |

---

## 🤝 Workflow de collaboration

### Pour le développeur qui modifie la config

1. **Modifier la configuration dans Keycloak**
   - Faire les changements nécessaires dans l'interface Admin
   - Ajouter des clients, rôles, utilisateurs, etc.

2. **Exporter la nouvelle configuration**
   ```bash
   # Voir section "Exporter la configuration Keycloak" ci-dessous
   ```

3. **Commiter et pusher**
   ```bash
   git add keycloak-config/realm-export.json
   git commit -m "feat(keycloak): ajout du client XYZ et rôle ABC"
   git push origin mohamed-hmidouch
   ```

### Pour le binôme qui récupère les changements

1. **Puller les changements**
   ```bash
   git pull origin mohamed-hmidouch
   ```

2. **Redémarrer Keycloak**
   ```bash
   # Arrêter Keycloak
   docker compose stop keycloak
   
   # Supprimer le conteneur (pour forcer le re-import)
   docker compose rm -f keycloak
   
   # Redémarrer Keycloak (il va re-importer le realm)
   docker compose up -d keycloak
   
   # Vérifier les logs
   docker logs -f keycloak
   ```

3. **Vérifier l'import**
   - Se connecter à la console Admin
   - Vérifier que les nouveaux changements sont présents

---

## 📤 Exporter la configuration Keycloak

### Méthode 1 : Export via l'interface Admin (Recommandé)

1. Se connecter à la console Admin : `http://localhost:8080/auth/admin`
2. Sélectionner le realm **logistics-realm**
3. Aller dans **Realm Settings** (menu de gauche)
4. Cliquer sur l'onglet **Action** (en haut)
5. Sélectionner **Partial Export**
6. **Cocher toutes les options** :
   - ✅ Export groups and roles
   - ✅ Export clients
   - ✅ Export users (⚠️ voir note ci-dessous)
7. Cliquer sur **Export**
8. Le fichier JSON est téléchargé

### Méthode 2 : Export via CLI (Avancé)

```bash
# Se connecter au conteneur Keycloak
docker exec -it keycloak bash

# Exporter le realm
/opt/keycloak/bin/kc.sh export \
  --dir /tmp \
  --realm logistics-realm \
  --users realm_file

# Copier le fichier exporté vers le host
exit
docker cp keycloak:/tmp/logistics-realm-realm.json ./keycloak-config/realm-export.json
```

### ⚠️ Important : Gestion des utilisateurs

**Option A : Export avec utilisateurs (Développement)**
- ✅ Pratique pour partager des comptes de test
- ⚠️ Ne pas commiter de vrais mots de passe en production
- Les mots de passe sont stockés en clair dans le JSON (pour l'import)

**Option B : Export sans utilisateurs (Production)**
- ✅ Plus sécurisé
- Chaque développeur crée ses propres utilisateurs de test
- Documenter les rôles requis dans ce fichier

### Remplacer le fichier de configuration

```bash
# Déplacer le fichier exporté vers le bon emplacement
mv ~/Downloads/realm-export.json ./keycloak-config/realm-export.json

# Vérifier que le fichier est valide JSON
cat keycloak-config/realm-export.json | jq '.' > /dev/null && echo "✅ JSON valide"
```

---

## 🔧 Modifier la configuration

### Ajouter un nouveau client

1. **Via l'interface Admin**
   - Realm Settings → Clients → Create
   - Configurer le client (redirect URIs, web origins, etc.)
   - Sauvegarder

2. **Exporter la config** (voir section précédente)

3. **Commiter**
   ```bash
   git add keycloak-config/realm-export.json
   git commit -m "feat(keycloak): ajout client logistics-mobile"
   git push
   ```

### Ajouter un nouveau rôle

1. **Via l'interface Admin**
   - Realm Settings → Roles → Create Role
   - Nom : `NEW_ROLE`
   - Description : Description du rôle
   - Sauvegarder

2. **Assigner aux utilisateurs**
   - Users → Sélectionner un utilisateur → Role Mappings
   - Ajouter le rôle

3. **Exporter et commiter**

### Modifier les paramètres du realm

Exemples de paramètres modifiables :
- Token lifespan
- Session timeouts
- Login settings (remember me, email verification, etc.)
- Password policies
- Brute force protection

Après modification → Exporter → Commiter

---

## 👥 Utilisateurs et rôles

### Rôles définis

| Rôle | Description | Permissions |
|------|-------------|-------------|
| **ADMIN** | Administrateur système | Accès complet à toutes les fonctionnalités |
| **WAREHOUSE_MANAGER** | Gestionnaire d'entrepôt | Gestion des stocks, produits, inventaires |
| **CLIENT** | Client | Consultation et gestion de ses commandes |

### Utilisateurs de test pré-configurés

Ces utilisateurs sont inclus dans le `realm-export.json` :

```
ADMIN :
- Email: admin1@system.com
- Mot de passe: adminpass
- Rôle: ADMIN

WAREHOUSE_MANAGER :
- Email: manager1@system.com
- Mot de passe: managerpass
- Rôle: WAREHOUSE_MANAGER

CLIENTS :
- Email: client1@system.com / Mot de passe: pass123 / Rôle: CLIENT
- Email: client2@system.com / Mot de passe: pass456 / Rôle: CLIENT
- Email: client3@system.com / Mot de passe: pass789 / Rôle: CLIENT
```

### Ajouter un nouvel utilisateur

**Via l'interface Admin** :
1. Users → Add User
2. Remplir : Username, Email, First/Last Name
3. Credentials → Set Password (décocher "Temporary")
4. Role Mappings → Assigner les rôles
5. Exporter la config

---

## 🔌 Clients configurés

### 1. logistics-backend

**Type** : Confidential Client (Resource Server)

**Configuration** :
- **Client ID** : `logistics-backend`
- **Client Protocol** : openid-connect
- **Access Type** : Confidential
- **Service Accounts Enabled** : Yes
- **Direct Access Grants** : Enabled
- **Valid Redirect URIs** : `http://localhost:8093/*`
- **Web Origins** : `http://localhost:8093`, `http://localhost:3000`, `http://localhost:4200`

**Usage** :
- Valide les JWT tokens envoyés par le frontend
- Peut obtenir un token pour les appels service-to-service

**Secret** :
- Le secret est généré automatiquement par Keycloak
- Visible dans : Clients → logistics-backend → Credentials

### 2. logistics-frontend

**Type** : Public Client (SPA/Web App)

**Configuration** :
- **Client ID** : `logistics-frontend`
- **Client Protocol** : openid-connect
- **Access Type** : Public
- **Standard Flow** : Enabled
- **Direct Access Grants** : Enabled
- **Valid Redirect URIs** : `http://localhost:3000/*`, `http://localhost:4200/*`
- **Web Origins** : `http://localhost:3000`, `http://localhost:4200`

**Usage** :
- Utilisé par l'application frontend (React, Angular, Vue, etc.)
- Authentifie les utilisateurs et obtient les JWT tokens

---

## 🔍 Troubleshooting

### Le realm n'est pas importé au démarrage

**Symptômes** :
- Le realm `logistics-realm` n'apparaît pas dans la console Admin
- Seul le realm `master` est présent

**Solutions** :

1. **Vérifier les logs**
   ```bash
   docker logs keycloak | grep -i import
   docker logs keycloak | grep -i error
   ```

2. **Vérifier le volume mount**
   ```bash
   docker exec keycloak ls -la /opt/keycloak/data/import/
   # Doit afficher : realm-export.json
   ```

3. **Forcer le re-import**
   ```bash
   docker compose down keycloak
   docker volume rm digitallogistics_dev_keycloak_data  # Attention : supprime les données
   docker compose up -d keycloak
   ```

4. **Vérifier le fichier JSON**
   ```bash
   # Le fichier doit être un JSON valide
   cat keycloak-config/realm-export.json | jq '.' > /dev/null
   
   # Vérifier que l'ID du realm est correct
   cat keycloak-config/realm-export.json | jq '.realm'
   # Doit afficher : "logistics-realm"
   ```

### Erreur "Realm with same name exists"

**Cause** : Le realm existe déjà (import précédent)

**Solution** :

**Option A : Supprimer le realm existant**
1. Console Admin → Realm Settings → Action → Delete
2. Redémarrer Keycloak

**Option B : Supprimer les données Keycloak**
```bash
docker compose down keycloak
docker volume rm digitallogistics_dev_keycloak_data
docker compose up -d keycloak
```

### Les utilisateurs ne peuvent pas se connecter

**Vérifications** :

1. **L'utilisateur est activé**
   - Users → Sélectionner l'utilisateur → Enabled = ON

2. **Email vérifié** (si requis)
   - Users → Sélectionner l'utilisateur → Email Verified = Yes

3. **Mot de passe correct**
   - Réinitialiser : Users → Credentials → Set Password

4. **Le client est correctement configuré**
   - Clients → logistics-frontend → Settings
   - Vérifier les Redirect URIs et Web Origins

### Erreur CORS lors de l'authentification

**Cause** : Web Origins mal configurés

**Solution** :
1. Clients → logistics-backend → Settings
2. Web Origins : Ajouter l'URL du frontend
   ```
   http://localhost:3000
   http://localhost:4200
   ```
3. Sauvegarder
4. Exporter et commiter

### Les rôles n'apparaissent pas dans le JWT

**Vérifications** :

1. **Protocol Mapper configuré**
   - Clients → logistics-backend → Client Scopes → roles
   - Vérifier que le mapper "roles" existe

2. **Rôles assignés à l'utilisateur**
   - Users → Sélectionner l'utilisateur → Role Mappings
   - Les rôles doivent être dans "Assigned Roles"

3. **Tester le token**
   ```bash
   # Obtenir un token
   curl -X POST http://localhost:8080/auth/realms/logistics-realm/protocol/openid-connect/token \
     -d "client_id=logistics-backend" \
     -d "client_secret=<CLIENT_SECRET>" \
     -d "grant_type=password" \
     -d "username=admin1@system.com" \
     -d "password=adminpass"
   
   # Décoder le JWT sur https://jwt.io
   # Vérifier la présence de "roles": ["ADMIN"]
   ```

---

## 📚 Ressources utiles

### Documentation officielle
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Server Administration Guide](https://www.keycloak.org/docs/latest/server_admin/)
- [Securing Applications Guide](https://www.keycloak.org/docs/latest/securing_apps/)

### Endpoints importants

```bash
# Well-known configuration
http://localhost:8080/auth/realms/logistics-realm/.well-known/openid-configuration

# JWKS (clés publiques pour valider les JWT)
http://localhost:8080/auth/realms/logistics-realm/protocol/openid-connect/certs

# Token endpoint
http://localhost:8080/auth/realms/logistics-realm/protocol/openid-connect/token

# Authorization endpoint
http://localhost:8080/auth/realms/logistics-realm/protocol/openid-connect/auth

# Introspection endpoint
http://localhost:8080/auth/realms/logistics-realm/protocol/openid-connect/token/introspect
```

### Commandes Docker utiles

```bash
# Voir les logs Keycloak
docker logs -f keycloak

# Redémarrer Keycloak uniquement
docker compose restart keycloak

# Se connecter au conteneur
docker exec -it keycloak bash

# Voir les variables d'environnement
docker exec keycloak env | grep KC_

# Vérifier la santé du service
curl http://localhost:8080/auth/health
```

---

## 📝 Notes importantes

### 🔒 Sécurité en Production

**⚠️ NE JAMAIS faire en production** :
- Utiliser les mots de passe par défaut (`admin`/`admin`)
- Commiter des secrets ou mots de passe réels
- Exporter les utilisateurs avec leurs credentials
- Utiliser `start-dev` (uniquement pour développement)

**✅ En production, faire** :
- Utiliser `start` au lieu de `start-dev`
- Configurer SSL/TLS
- Utiliser des secrets forts et uniques
- Activer la vérification d'email
- Configurer SMTP pour les emails
- Activer la protection contre le brute force
- Utiliser une base de données dédiée pour Keycloak

### 🔄 Mises à jour du realm

**Workflow recommandé** :
1. Faire les modifications dans un environnement de dev/test
2. Tester complètement
3. Exporter la configuration
4. Faire une pull request avec le `realm-export.json`
5. Après validation, merger sur la branche principale
6. L'équipe pull et redémarre Keycloak

### 📦 Versioning du realm

Vous pouvez versionner les exports pour garder un historique :

```bash
# Créer un backup avant modification
cp keycloak-config/realm-export.json keycloak-config/realm-export-backup-$(date +%Y%m%d).json

# Restaurer un backup
cp keycloak-config/realm-export-backup-20241230.json keycloak-config/realm-export.json
docker compose restart keycloak
```

---

## 🆘 Support

En cas de problème :
1. Consulter la section Troubleshooting ci-dessus
2. Vérifier les logs : `docker logs keycloak`
3. Consulter la documentation officielle Keycloak
4. Contacter l'équipe sur le canal de communication du projet

---

**Dernière mise à jour** : 30 Décembre 2025  
**Version Keycloak** : 24.0  
**Auteur** : Mohamed Hmidouch
