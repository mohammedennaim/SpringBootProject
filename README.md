# 🚀 Digital Logistics API

<div align="center">

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**API REST moderne pour la gestion logistique complète**

[Fonctionnalités](#-fonctionnalités) • [Installation](#-installation-rapide) • [Documentation](#-documentation-api) • [Architecture](#-architecture)

</div>

---

## 📋 Table des Matières

- [À Propos](#-à-propos)
- [Fonctionnalités](#-fonctionnalités)
- [Stack Technologique](#-stack-technologique)
- [Installation Rapide](#-installation-rapide)
- [Configuration](#-configuration)
- [Documentation API](#-documentation-api)
- [Architecture](#-architecture)
- [Tests](#-tests)
- [CI/CD](#-cicd)
- [Sécurité](#-sécurité)
- [Contribution](#-contribution)

---

## 🎯 À Propos

**Digital Logistics** est une API RESTful complète construite avec Spring Boot 3.5.7 pour gérer l'ensemble des opérations logistiques d'une entreprise moderne.

### Cas d'Usage
- 📦 Gestion multi-entrepôts
- 🛒 Traitement des commandes (vente & achat)
- 📊 Suivi d'inventaire en temps réel
- 🚚 Gestion des expéditions et transporteurs
- 👥 Gestion des utilisateurs avec rôles (ADMIN, MANAGER, CLIENT)
- 📈 Rapports statistiques et analytics

---

## ✨ Fonctionnalités

### 🔐 Authentification & Sécurité
- ✅ JWT stateless avec révocation de tokens
- ✅ Autorisation basée sur les rôles (RBAC)
- ✅ Hachage BCrypt des mots de passe
- ✅ Tokens persistés en base de données
- ✅ Nettoyage automatique des tokens expirés

### 👥 Gestion des Utilisateurs
- ✅ CRUD complet (Admins, Managers, Clients)
- ✅ Validation d'unicité des emails
- ✅ Filtrage par rôle et statut
- ✅ Assignation d'entrepôts aux managers

### 📦 Gestion des Produits & Inventaire
- ✅ CRUD produits avec pagination
- ✅ Recherche par SKU
- ✅ Suivi des stocks (disponible, réservé)
- ✅ Mouvements d'inventaire (INBOUND, OUTBOUND, ADJUSTMENT)
- ✅ Alertes de réapprovisionnement

### 🛒 Gestion des Commandes
- ✅ Commandes de vente avec réservation automatique
- ✅ Commandes fournisseurs avec réception
- ✅ Statuts multiples (CREATED, RESERVED, SHIPPED, DELIVERED, CANCELLED)
- ✅ Lignes de commande détaillées

### 🚚 Gestion des Expéditions
- ✅ CRUD expéditions
- ✅ Suivi par numéro de tracking
- ✅ Gestion des transporteurs
- ✅ Statuts d'expédition (PLANNED, IN_TRANSIT, DELIVERED)

### 📊 Rapports & Analytics
- ✅ Rapport de commandes (taux de livraison, revenus)
- ✅ Rapport d'inventaire (ruptures, surstocks, rotation)
- ✅ Rapport d'expéditions (performance transporteurs)
- ✅ Filtrage par dates et entrepôts

---

## 🛠️ Stack Technologique

| Composant | Technologie | Version |
|-----------|-------------|---------|
| **Framework** | Spring Boot | 3.5.7 |
| **Langage** | Java | 17 |
| **Base de données** | PostgreSQL | 16 |
| **Build Tool** | Maven | 3.9+ |
| **Mapping DTO** | MapStruct | 1.6.2 |
| **Boilerplate** | Lombok | 1.18.34 |
| **Sécurité** | Spring Security + JWT | 0.11.5 |
| **Documentation** | SpringDoc OpenAPI | 2.8.0 |
| **Pool Connexions** | HikariCP | Intégré |
| **Conteneurisation** | Docker + Compose | Latest |
| **CI/CD** | Jenkins | LTS |
| **Quality** | SonarQube | Community |

---

## 🚀 Installation Rapide

### Prérequis
- **Java 17+**
- **Docker & Docker Compose**
- **Maven 3.9+** (ou utiliser `./mvnw`)

### Option 1: Docker Compose (Recommandé)

```bash
# 1. Cloner le projet
git clone https://github.com/votre-username/digital-logistics.git
cd digital-logistics

# 2. Copier et configurer .env
cp .env.example .env
# Éditer .env avec vos valeurs

# 3. Démarrer tous les services
docker-compose up -d

# 4. Vérifier les services
docker-compose ps
```

**Services disponibles:**
- 🚀 **API**: http://localhost:8093
- 📚 **Swagger**: http://localhost:8093/swagger-ui.html
- 🗄️ **pgAdmin**: http://localhost:8081
- 📊 **SonarQube**: http://localhost:9001
- 🔧 **Jenkins**: http://localhost:8089

### Option 2: Exécution Locale

```bash
# 1. Démarrer PostgreSQL
docker run -d -p 5432:5432 \
  -e POSTGRES_DB=digital_logistics \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  postgres:16-alpine

# 2. Compiler et lancer
./mvnw clean package -DskipTests
java -jar target/digital-logistics-1.0.0.jar
```

---

## ⚙️ Configuration

### Variables d'Environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `SERVER_PORT` | Port de l'application | `8093` |
| `DB_URL` | URL PostgreSQL | `jdbc:postgresql://localhost:5432/digital_logistics` |
| `DB_USERNAME` | Utilisateur DB | `postgres` |
| `DB_PASSWORD` | Mot de passe DB | `password` |
| `JWT_SECRET` | Clé secrète JWT (min 32 chars) | - |
| `JWT_EXPIRATION` | Durée validité token (ms) | `3600000` (1h) |

### Profils Spring

```bash
# Développement (par défaut)
./mvnw spring-boot:run

# Production
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 📚 Documentation API

### Swagger UI
Accédez à la documentation interactive:
```
http://localhost:8093/swagger-ui.html
```

### Endpoints Principaux

#### Authentification
```bash
# Register
POST /api/auth/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "contact": "+1234567890"
}

# Login
POST /api/auth/login
{
  "email": "john@example.com",
  "password": "SecurePass123!"
}
# Response: { "token": "eyJhbGc..." }

# Logout
POST /api/auth/logout
Authorization: Bearer <token>
```

#### Produits
```bash
# Liste paginée
GET /api/products?page=0&size=20&search=laptop

# Créer
POST /api/products
Authorization: Bearer <token>
{
  "name": "Laptop Dell",
  "sku": "DELL-001",
  "price": 999.99
}
```

#### Commandes
```bash
# Créer commande de vente
POST /api/sales-orders
Authorization: Bearer <token>
{
  "clientId": "uuid",
  "lines": [
    {
      "productId": "uuid",
      "quantity": 2
    }
  ]
}
```

---

## 🏗️ Architecture

### Structure du Projet
```
digital-logistics/
├── src/main/java/com/example/digitallogistics/
│   ├── config/              # Configuration (Security, Swagger, CORS)
│   ├── controller/          # REST Controllers
│   ├── model/
│   │   ├── entity/         # Entités JPA
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── enums/          # Énumérations
│   │   └── mapper/         # Mappers MapStruct
│   ├── repository/         # Spring Data JPA
│   ├── service/            # Logique métier
│   ├── security/           # JWT & Spring Security
│   ├── exception/          # Gestion des erreurs
│   ├── util/               # Utilitaires
│   └── jobs/               # Tâches planifiées
├── src/main/resources/
│   ├── application.yml     # Configuration principale
│   ├── application-prod.yml # Configuration production
│   └── logback-spring.xml  # Configuration logs
└── src/test/               # Tests unitaires & intégration
```

### Principes Architecturaux
- ✅ **Architecture en couches** (Controller → Service → Repository)
- ✅ **Séparation des préoccupations** (DTOs vs Entities)
- ✅ **Injection de dépendances** (Spring IoC)
- ✅ **Héritage JPA** (Strategy JOINED pour User)
- ✅ **RESTful API** (HTTP verbs, status codes)

---

## 🧪 Tests

### Exécuter les Tests
```bash
# Tous les tests
./mvnw test

# Tests spécifiques
./mvnw test -Dtest=CarrierControllerTest

# Avec couverture JaCoCo
./mvnw clean test jacoco:report
```

### Couverture de Code
- **Controllers**: 13 tests
- **Services**: 12 tests
- **Rapport JaCoCo HTML**: `target/site/jacoco/index.html`
- **Rapport JaCoCo XML** (pour SonarQube): `target/site/jacoco/jacoco.xml`

---

## 🔄 CI/CD

### Jenkins Pipeline
Le projet inclut un `Jenkinsfile` avec:
- ✅ Build & compilation
- ✅ Tests unitaires
- ✅ Rapport JaCoCo
- ✅ Analyse SonarQube (optionnel)
- ✅ Packaging JAR
- ✅ Archivage des artefacts

### Lancer Jenkins
```bash
docker-compose up -d jenkins
# Accès: http://localhost:8089
```

### SonarQube - Analyse de Qualité du Code

#### Démarrer SonarQube
```bash
# Démarrer le service
docker-compose up -d sonar

# Attendre que SonarQube soit prêt (90 secondes)
docker-compose logs -f sonar

# Accès: http://localhost:9001
# Login par défaut: admin / admin
```

#### Lancer l'Analyse

**Option 1: Script automatique (Recommandé)**
```bash
# Windows
run-sonar.bat [SONAR_TOKEN]

# Linux/Mac
chmod +x run-sonar.sh
./run-sonar.sh [SONAR_TOKEN]
```

**Option 2: Commande Maven**
```bash
# Avec login/password (première fois)
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9001 \
  -Dsonar.login=admin \
  -Dsonar.password=admin

# Avec token (recommandé pour CI/CD)
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9001 \
  -Dsonar.login=YOUR_TOKEN
```

#### Générer un Token SonarQube
1. Connectez-vous à http://localhost:9001
2. Allez dans **My Account** → **Security**
3. Générez un nouveau token
4. Utilisez-le dans vos commandes Maven

#### Résultats de l'Analyse
Après l'analyse, consultez:
- **Dashboard**: http://localhost:9001/dashboard?id=digital-logistics
- **Bugs & Vulnerabilités**: Onglet "Issues"
- **Code Coverage**: Onglet "Coverage"
- **Code Smells**: Onglet "Maintainability"

---

## 🔒 Sécurité

### Bonnes Pratiques Implémentées
- ✅ **JWT avec révocation** (tokens en DB)
- ✅ **BCrypt** pour les mots de passe
- ✅ **RBAC** (Role-Based Access Control)
- ✅ **User non-root** dans Docker
- ✅ **CORS** configuré
- ✅ **Validation** des entrées
- ✅ **Gestion des erreurs** centralisée

### Recommandations Production
```bash
# Changer JWT_SECRET
JWT_SECRET=$(openssl rand -base64 32)

# Utiliser HTTPS
# Configurer rate limiting
# Activer audit logging
```

---

## 📊 Monitoring

### Health Check
```bash
curl http://localhost:8093/api/health
```

### Logs
```bash
# Voir les logs
tail -f logs/digital-logistics.log

# Logs Docker
docker-compose logs -f app
```

---

## 🤝 Contribution

1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit (`git commit -m 'Add AmazingFeature'`)
4. Push (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

---

## 📝 License

Ce projet est sous licence MIT. Voir [LICENSE](LICENSE) pour plus de détails.

---

## 📞 Contact

**Votre Nom** - [@votre_twitter](https://twitter.com/votre_twitter)

Project Link: [https://github.com/votre-username/digital-logistics](https://github.com/votre-username/digital-logistics)

---

<div align="center">

**⭐ Si ce projet vous aide, n'hésitez pas à lui donner une étoile !**

Made with ❤️ by [Votre Nom]

</div>
