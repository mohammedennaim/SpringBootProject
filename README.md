# Digital Logistics API# Digital Logistics — README



[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)Bref: API Spring Boot pour la gestion logistique (PostgreSQL). Ce dépôt contient les entités JPA, repositories Spring Data, services et controllers minimalistes. Le projet est prévu pour être exécuté en local via Maven ou via Docker Compose (Postgres + pgAdmin + app).

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)

[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)## Contenu principal

[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)- `pom.xml` — configuration Maven (Spring Boot 3.5.x, Java 17, MapStruct, Lombok)

- `Dockerfile` — Dockerfile de développement (lance `mvn spring-boot:run` pour live-reload)

> **API REST complète pour la gestion logistique** — Gestion d'entrepôts, produits, commandes, expéditions, inventaire et rapports statistiques avec authentification JWT et documentation Swagger.- `docker-compose.yaml` — services: `postgres`, `pgadmin`, `app` (monorepo image)

- `src/main/java/com/example/digitallogistics` — code source Java

---  - `LogisticsApiApplication.java` — point d'entrée Spring Boot

  - `controller/` — controllers REST (ex: `UserController`, `TestController`)

## 📋 Table des matières  - `model/` — `entity/`, `dto/`, `enums/`, `mapper/`

  - `repository/` — Spring Data JPA repositories

- [À propos](#-à-propos)  - `service/` — services métiers

- [Fonctionnalités](#-fonctionnalités)  - `config/`, `exception/`, `jobs/`, `util/`

- [Architecture](#-architecture)- `src/main/resources/application.yml` — configuration Spring Boot

- [Stack technologique](#-stack-technologique)- `src/main/resources/data.sql` — script d'initialisation (DDL + INSERTs) monté dans Postgres container

- [Prérequis](#-prérequis)

- [Installation](#-installation)## Structure de fichiers (extraits)

- [Configuration](#-configuration)

- [Utilisation](#-utilisation)```

- [Endpoints API](#-endpoints-api)docker-compose.yaml

- [Modèle de données](#-modèle-de-données)Dockerfile

- [Sécurité](#-sécurité)pom.xml

- [Tests](#-tests)src/

- [Déploiement](#-déploiement)  main/

- [Troubleshooting](#-troubleshooting)    java/com/example/digitallogistics/

      controller/

---        UserController.java

        TestController.java

## 🚀 À propos      model/

        entity/

**Digital Logistics** est une API RESTful moderne construite avec Spring Boot 3.5.7, conçue pour gérer l'ensemble des opérations logistiques d'une entreprise :        enums/

        dto/

- **Gestion des utilisateurs** : Administrateurs, managers d'entrepôt, clients avec rôles distincts      repository/

- **Gestion d'inventaire** : Suivi des produits, stocks, mouvements d'inventaire      service/

- **Gestion des commandes** : Commandes d'achat, commandes de vente avec lignes de commande    resources/

- **Gestion des expéditions** : Suivi des livraisons, transporteurs, statuts d'expédition      application.yml

- **Rapports statistiques** : Analyse des commandes, inventaire, expéditions, mouvements      data.sql

- **Sécurité JWT** : Authentification robuste avec autorisation basée sur les rôles```



---## Endpoints importants (User)

Base path: `/api/users`

## ✨ Fonctionnalités

- GET `/api/users` — liste tous les users (200 OK)

### 🔐 Authentification & Autorisation- GET `/api/users/{id}` — récupère user par UUID (200 / 404)

- JWT (JSON Web Token) pour l'authentification stateless- GET `/api/users/by-email?email=...` — récupère par email (200 / 404)

- 3 rôles utilisateurs : `ADMIN`, `WAREHOUSE_MANAGER`, `CLIENT`- GET `/api/users/role/{role}` — récupère users par rôle (200, peut être vide)

- Autorisation granulaire par endpoint avec `@PreAuthorize`- POST `/api/users` — crée un user (201 Created + Location header)

- Gestion de révocation de tokens- PUT `/api/users/{id}` — met à jour un user (200 / 404)

- Enregistrement de nouveaux clients- DELETE `/api/users/{id}` — supprime (204 No Content)

- Hachage sécurisé des mots de passe (BCrypt)

Exemples (adapter le port et UUID):

### 👥 Gestion des utilisateurs

- **Managers** (CRUD complet, ADMIN uniquement)```bash

  - Création, lecture, mise à jour, suppression de managers# lister

  - Filtrage par entrepôt et statut actifcurl -i http://localhost:8080/api/users

  - Validation d'unicité des emails

- **Clients** (CRUD complet)# récupérer par id

  - Liste, création, détails, mise à jour de clientscurl -i http://localhost:8080/api/users/123e4567-e89b-12d3-a456-426614174000

  - Gestion des informations de contact

  - Accès sécurisé par rôle# récupérer par email

curl -i "http://localhost:8080/api/users/by-email?email=someone@example.com"

### 📦 Gestion des produits

- CRUD complet des produits# récupérer par role

- Pagination et filtrage (recherche, statut actif)curl -i http://localhost:8080/api/users/role/ADMIN

- Recherche par SKU

- Gestion des stocks par entrepôt# créer

curl -i -H 'Content-Type: application/json' -d '{"id":"<uuid>","email":"a@b.com","password":"pass","role":"ADMIN","active":true}' http://localhost:8080/api/users

### 🏢 Gestion des entrepôts

- CRUD des entrepôts```

- Gestion des localisations

- Suivi de capacité et inventaire> Remarque: le mapping pour le rôle est `/role/{role}` (évite l'ambiguïté avec `/{id}`).



### 📊 Rapports statistiques## Build & exécution

- **Rapport de commandes** : Taux de livraison, backorders, revenus totaux

- **Rapport d'inventaire** : État des stocks, ruptures, surstocks, rotation1) En local (Maven)

- **Rapport d'expéditions** : Performance par transporteur, livraison à temps

- **Rapport de mouvements** : Analyse des entrées/sorties d'inventaire```bash

- Filtrage par dates et entrepôts# compiler et packager

./mvnw -DskipTests package

### 🚚 Gestion des expéditions

- CRUD des expéditions# exécuter localement (jar)

- Suivi des statuts (PENDING, IN_TRANSIT, DELIVERED, CANCELLED)java -jar target/digital-logistics-1.0.0.jar

- Pagination et filtrage avancé```

- Association avec transporteurs et commandes

2) Avec Docker Compose (développement)

### 📄 Documentation interactive

- Interface Swagger UI intégrée- Copier `.env.example` en `.env` et ajuster les variables (`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `SERVER_PORT`, ...)

- Documentation OpenAPI 3.0- Démarrer les services:

- Test des endpoints directement depuis le navigateur

```bash

---docker compose up --build -d

```

## 🏗️ Architecture

- Par défaut le service `app` lit `SERVER_PORT` (défini dans `.env`). Vérifiez le port exposé dans la sortie de `docker compose ps`.

```

digital-logistics/Notes importantes:

├── src/- Le `Dockerfile` fourni est orienté développement: il exécute `mvn spring-boot:run` et inclut `spring-boot-devtools`. Cela provoque parfois des redémarrages (devtools) et des réponses vides lors des rebuilds. Pour un container stable (production-like), préférez changer le Dockerfile pour exécuter le jar repackagé : `java -jar target/*.jar`.

│   ├── main/

│   │   ├── java/com/example/digitallogistics/Exemple de Dockerfile (extrait recommandé pour build d'image finale):

│   │   │   ├── LogisticsApiApplication.java    # Point d'entrée Spring Boot

│   │   │   ├── config/                          # Configuration (Security, Swagger, etc.)```dockerfile

│   │   │   │   ├── SecurityConfig.java         # Configuration JWT & Spring SecurityFROM eclipse-temurin:17-jdk-alpine

│   │   │   │   ├── SwaggerConfig.java          # Configuration OpenAPI/SwaggerWORKDIR /app

│   │   │   │   └── ...COPY target/digital-logistics-1.0.0.jar app.jar

│   │   │   ├── controller/                      # Controllers RESTEXPOSE 8080

│   │   │   │   ├── AuthController.java         # Authentification (login, register, logout)ENTRYPOINT ["java","-jar","/app/app.jar"]

│   │   │   │   ├── ManagerController.java      # CRUD Managers (ADMIN only)```

│   │   │   │   ├── ClientController.java       # CRUD Clients

│   │   │   │   ├── ProductController.java      # CRUD Produits3) Re-créer la DB seed

│   │   │   │   ├── WarehouseController.java    # CRUD Entrepôts

│   │   │   │   ├── ShipmentController.java     # Gestion expéditions- Le script `src/main/resources/data.sql` est monté dans `/docker-entrypoint-initdb.d/` du container Postgres. Il ne s'exécute que lors de l'initialisation d'un volume neuf. Pour ré-appliquer le seed, supprimez le volume `pg_data` puis relancez `docker compose up`:

│   │   │   │   ├── ReportController.java       # Rapports statistiques

│   │   │   │   └── ...```bash

│   │   │   ├── model/docker compose down

│   │   │   │   ├── entity/                     # Entités JPAdocker volume rm <project>_pg_data  # ou `docker volume ls` pour trouver le nom

│   │   │   │   │   ├── User.java              # Classe abstraite (héritage JOINED)docker compose up --build

│   │   │   │   │   ├── Manager.java           # Manager extends User```

│   │   │   │   │   ├── Client.java            # Client extends User

│   │   │   │   │   ├── Product.java## Debugging courant

│   │   │   │   │   ├── Warehouse.java

│   │   │   │   │   ├── Inventory.java- `No static resource api/users.`: cela signifie qu'aucun controller ne correspond au chemin demandé — souvent dû à une route mal mappée ou au controller non chargé. Vérifiez les logs (`docker compose logs app --tail 200`) ; cherchez `Tomcat started` et `Started LogisticsApiApplication`.

│   │   │   │   │   ├── SalesOrder.java- `Empty reply from server` pendant les rebuilds: fréquent si devtools/maven-run à l'intérieur du container redémarre le processus. Solution: lancer le jar directement pour stabilité.

│   │   │   │   │   ├── Shipment.java

│   │   │   │   │   └── ...## Tests

│   │   │   │   ├── dto/                        # Data Transfer Objects

│   │   │   │   ├── enums/                      # Énumérations (Role, Status, etc.)- Les dépendances de test sont configurées (`spring-boot-starter-test`). Les tests unitaires se trouvent sous `src/test`. Pour lancer les tests:

│   │   │   │   └── mapper/                     # Mappers DTO <-> Entity (MapStruct + manuels)

│   │   │   ├── repository/                      # Spring Data JPA Repositories```bash

│   │   │   ├── service/                         # Services métiers (interfaces + implémentations)./mvnw test

│   │   │   ├── exception/                       # Gestion des exceptions```

│   │   │   ├── util/                            # Utilitaires (JWT, etc.)

│   │   │   └── jobs/                            # Tâches planifiées (optionnel)## Prochaines améliorations suggérées

│   │   └── resources/

│   │       ├── application.yml                  # Configuration Spring Boot- Convertir `data.sql` en migrations Flyway/Liquibase pour un contrôle plus sûr des migrations.

│   │       └── data.sql                         # Script d'initialisation DB (optionnel)- Remplacer l'exécution `mvn spring-boot:run` dans Docker par le jar packagé pour stabilité en conteneur.

│   └── test/                                     # Tests unitaires et d'intégration- Ajouter DTOs + MapStruct mappers pour séparer entités et API.

├── target/                                       # Artefacts de build Maven- Ajouter authentification / hashing des mots de passe avant stockage.

├── Dockerfile                                    # Image Docker multi-stage

├── docker-compose.yaml                           # Orchestration Docker (PostgreSQL, pgAdmin, app)## Contacts / références

├── pom.xml                                       # Configuration Maven

└── README.md                                     # Ce fichier- Point d'entrée: `src/main/java/com/example/digitallogistics/LogisticsApiApplication.java`

```- Fichier d'initialisation DB: `src/main/resources/data.sql`



**Principes architecturaux :**---

- **Architecture en couches** : Controller → Service → Repository → Database

- **Séparation des préoccupations** : DTOs pour l'API, Entities pour la persistanceSi vous voulez, je peux :

- **Injection de dépendances** : Spring IoC/DI- générer automatiquement un `README.md` plus détaillé avec tous les endpoints (tous les controllers), ou

- **Héritage JPA** : Strategy JOINED pour User/Manager/Client- modifier le Dockerfile pour produire une image basée sur le jar (stable) et adapter `docker-compose.yaml` (je peux appliquer ce changement et redéployer).

- **Sécurité** : Filter chain JWT avant UsernamePasswordAuthenticationFilter

Dites-moi quelle option vous préférez (générer plus de docs OU adapter Docker pour exécuter le jar). 
---

## 🛠️ Stack technologique

| Composant | Technologie | Version |
|-----------|-------------|---------|
| **Framework** | Spring Boot | 3.5.7 |
| **Langage** | Java | 17 |
| **Base de données** | PostgreSQL | 16 (Alpine) |
| **Build Tool** | Maven | 3.9.5 |
| **Mapping DTO** | MapStruct | 1.6.2 |
| **Boilerplate** | Lombok | 1.18.34 |
| **ORM** | Hibernate | 6.6.33.Final |
| **Sécurité** | Spring Security + JWT | 3.5.7 |
| **Documentation** | SpringDoc OpenAPI | 2.7.0 |
| **Pool de connexions** | HikariCP | Intégré |
| **Validation** | Hibernate Validator | Intégré |
| **Conteneurisation** | Docker + Docker Compose | Latest |
| **Base Image** | Eclipse Temurin | 17-JDK/JRE |

**Dépendances clés :**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.6</version>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.7.0</version>
    </dependency>
</dependencies>
```

---

## 📋 Prérequis

### Pour Docker (Recommandé)
- **Docker** : Version 20.10+
- **Docker Compose** : Version 2.0+

### Pour exécution locale
- **Java JDK** : Version 17 ou supérieure
- **Maven** : Version 3.9+ (ou utiliser `./mvnw` inclus)
- **PostgreSQL** : Version 14+ (serveur local)

---

## 📦 Installation

### Docker (Recommandé)

#### Méthode 1 : Build et exécution simple

```bash
# 1. Cloner le repository
git clone https://github.com/votre-username/digital-logistics.git
cd digital-logistics

# 2. Build de l'image Docker
docker build -t digital-logistics-app .

# 3. Lancer PostgreSQL
docker run -d \
  --name postgres-logistics \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -e POSTGRES_DB=digital_logistics \
  -p 5432:5432 \
  postgres:16-alpine

# 4. Lancer l'application
docker run -d \
  --name digital-logistics-app \
  -p 8090:8090 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/digital_logistics \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=password \
  digital-logistics-app

# 5. Vérifier les logs
docker logs -f digital-logistics-app
```

#### Méthode 2 : Docker Compose (Multi-services)

```bash
# 1. Créer le fichier .env
cat > .env << EOF
# PostgreSQL
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password
POSTGRES_DB=digital_logistics
POSTGRES_PORT=5432

# pgAdmin
PGADMIN_DEFAULT_EMAIL=admin@example.com
PGADMIN_DEFAULT_PASSWORD=admin
PGADMIN_PORT=5050

# Application
SERVER_PORT=8090
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/digital_logistics
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
EOF

# 2. Démarrer tous les services
docker-compose up --build -d

# 3. Vérifier l'état
docker-compose ps

# 4. Voir les logs
docker-compose logs -f app
```

**Services disponibles :**
- **Application** : http://localhost:8090
- **Swagger UI** : http://localhost:8090/swagger-ui/index.html
- **pgAdmin** : http://localhost:5050

### Local (Maven)

```bash
# 1. Cloner le repository
git clone https://github.com/votre-username/digital-logistics.git
cd digital-logistics

# 2. Configurer PostgreSQL local
createdb digital_logistics
# Ou via psql:
# psql -U postgres -c "CREATE DATABASE digital_logistics;"

# 3. Configurer les variables d'environnement (optionnel)
export DB_URL=jdbc:postgresql://localhost:5432/digital_logistics
export DB_USERNAME=postgres
export DB_PASSWORD=your_password

# 4. Build du projet
./mvnw clean package -DskipTests

# 5. Exécuter l'application
java -jar target/digital-logistics-1.0.0.jar

# Ou directement avec Maven
./mvnw spring-boot:run
```

---

## ⚙️ Configuration

### Variables d'environnement

| Variable | Description | Valeur par défaut |
|----------|-------------|-------------------|
| `SERVER_PORT` | Port d'écoute de l'application | `8090` |
| `DB_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/logistics_db` |
| `DB_USERNAME` | Utilisateur PostgreSQL | `postgres` |
| `DB_PASSWORD` | Mot de passe PostgreSQL | `postgres` |
| `JWT_SECRET` | Clé secrète JWT (min 32 caractères) | `YourVerySecure...` |
| `JWT_EXPIRATION` | Durée de validité du token (ms) | `3600000` (1h) |
| `SHOW_SQL` | Afficher les requêtes SQL | `false` |

### Fichier application.yml

```yaml
spring:
  application:
    name: digital-logistics
  
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/logistics_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
  
  jpa:
    hibernate:
      ddl-auto: update  # Utiliser 'validate' en production
    show-sql: false

app:
  jwt:
    secret: ${JWT_SECRET:YourVerySecureRandomSecretKeyHere}
    expiration-ms: ${JWT_EXPIRATION:3600000}

server:
  port: ${SERVER_PORT:8090}

springdoc:
  api-docs:
    enabled: true
  swagger-ui:
    enabled: true
    path: /swagger-ui.html
```

**⚠️ Sécurité en production :**
- Changer `JWT_SECRET` avec une valeur forte (min. 32 caractères aléatoires)
- Utiliser `ddl-auto: validate` au lieu de `update`
- Désactiver `show-sql`
- Utiliser HTTPS
- Configurer CORS si nécessaire

---

## 🎯 Utilisation

### API Documentation (Swagger)

Accéder à l'interface Swagger UI pour tester les endpoints :

**URL :** http://localhost:8090/swagger-ui/index.html

**OpenAPI JSON :** http://localhost:8090/v3/api-docs

### Authentification

#### 1. Créer un compte (Register)

```bash
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "contact": "+1234567890"
}
```

**Réponse :**
```json
{
  "id": "uuid-here",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "CLIENT",
  "active": true
}
```

#### 2. Se connecter (Login)

```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePass123!"
}
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 3. Utiliser le token

```bash
GET /api/products
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📡 Endpoints API

### 🔐 Authentification (`/api/auth`)

| Méthode | Endpoint | Description | Rôles |
|---------|----------|-------------|-------|
| POST | `/api/auth/register` | Créer un nouveau client | Public |
| POST | `/api/auth/login` | Se connecter (obtenir JWT) | Public |
| POST | `/api/auth/logout` | Révoquer un token | Authentifié |

---

### 👤 Managers (`/api/managers`)

| Méthode | Endpoint | Description | Rôles |
|---------|----------|-------------|-------|
| GET | `/api/managers` | Liste tous les managers | ADMIN |
| GET | `/api/managers/{id}` | Détails d'un manager | ADMIN |
| GET | `/api/managers/warehouse/{warehouseId}` | Managers par entrepôt | ADMIN |
| GET | `/api/managers/active` | Managers actifs | ADMIN |
| POST | `/api/managers` | Créer un manager | ADMIN |
| PUT | `/api/managers/{id}` | Mettre à jour un manager | ADMIN |
| DELETE | `/api/managers/{id}` | Supprimer un manager | ADMIN |

**Exemple de création :**
```bash
POST /api/managers
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "email": "manager@example.com",
  "password": "ManagerPass123!",
  "name": "Manager Name",
  "warehouseId": "warehouse-uuid-here",
  "active": true
}
```

---

### 👥 Clients (`/api/clients`)

| Méthode | Endpoint | Description | Rôles |
|---------|----------|-------------|-------|
| GET | `/api/clients` | Liste tous les clients | ADMIN, WAREHOUSE_MANAGER |
| POST | `/api/clients` | Créer un client | ADMIN, WAREHOUSE_MANAGER |
| GET | `/api/clients/{id}` | Détails d'un client | ADMIN, WAREHOUSE_MANAGER, DRIVER |
| PUT | `/api/clients/{id}` | Mettre à jour un client | ADMIN, WAREHOUSE_MANAGER |

---

### 📦 Produits (`/api/products`)

| Méthode | Endpoint | Description | Rôles |
|---------|----------|-------------|-------|
| GET | `/api/products?page=0&size=20&search=keyword&active=true` | Liste paginée/filtrée | ADMIN, WAREHOUSE_MANAGER |
| GET | `/api/products/{id}` | Détails d'un produit | ADMIN, WAREHOUSE_MANAGER |
| GET | `/api/products/search?sku={sku}` | Recherche par SKU | ADMIN, WAREHOUSE_MANAGER |
| POST | `/api/products` | Créer un produit | ADMIN, WAREHOUSE_MANAGER |
| PUT | `/api/products/{id}` | Mettre à jour un produit | ADMIN, WAREHOUSE_MANAGER |
| PATCH | `/api/products/{id}/status` | Modifier statut (actif/inactif) | ADMIN, WAREHOUSE_MANAGER |
| DELETE | `/api/products/{id}` | Supprimer un produit | ADMIN |

---

### 🏢 Entrepôts (`/api/warehouses`)

| Méthode | Endpoint | Description | Rôles |
|---------|----------|-------------|-------|
| GET | `/api/warehouses` | Liste tous les entrepôts | ADMIN, WAREHOUSE_MANAGER |
| POST | `/api/warehouses` | Créer un entrepôt | ADMIN, WAREHOUSE_MANAGER |
| GET | `/api/warehouses/{id}` | Détails d'un entrepôt | ADMIN, WAREHOUSE_MANAGER |
| PUT | `/api/warehouses/{id}` | Mettre à jour un entrepôt | ADMIN, WAREHOUSE_MANAGER |
| DELETE | `/api/warehouses/{id}` | Supprimer un entrepôt | ADMIN |

---

### 🚚 Expéditions (`/api/shipments`)

| Méthode | Endpoint | Description | Rôles |
|---------|----------|-------------|-------|
| GET | `/api/shipments?page=0&size=20&status=PENDING&warehouseId=uuid` | Liste paginée/filtrée | ADMIN, WAREHOUSE_MANAGER |
| GET | `/api/shipments/{id}` | Détails d'une expédition | ADMIN, WAREHOUSE_MANAGER |
| POST | `/api/shipments` | Créer une expédition | ADMIN, WAREHOUSE_MANAGER |
| PUT | `/api/shipments/{id}` | Mettre à jour une expédition | ADMIN, WAREHOUSE_MANAGER |
| PATCH | `/api/shipments/{id}/status` | Changer le statut | ADMIN, WAREHOUSE_MANAGER |
| DELETE | `/api/shipments/{id}` | Annuler une expédition | ADMIN |

**Statuts disponibles :** `PENDING`, `IN_TRANSIT`, `DELIVERED`, `CANCELLED`

---

### 📊 Rapports (`/api/reports`)

| Méthode | Endpoint | Description | Rôles |
|---------|----------|-------------|-------|
| GET | `/api/reports/orders?fromDate=2024-10-01&toDate=2024-11-04` | Statistiques des commandes | ADMIN, WAREHOUSE_MANAGER |
| GET | `/api/reports/inventory?warehouseId=uuid` | Rapport d'inventaire | ADMIN, WAREHOUSE_MANAGER |
| GET | `/api/reports/shipments?carrierId=uuid&fromDate=...&toDate=...` | Performance des expéditions | ADMIN, WAREHOUSE_MANAGER |
| GET | `/api/reports/movements?warehouseId=uuid&fromDate=...&toDate=...` | Mouvements d'inventaire | ADMIN, WAREHOUSE_MANAGER |

**Exemple de rapport de commandes :**
```json
{
  "totalOrders": 245,
  "deliveredOrders": 230,
  "deliveryRate": 93.88,
  "backorders": 15,
  "totalRevenue": 125000.50,
  "averageOrderValue": 510.20,
  "period": {
    "from": "2024-10-01",
    "to": "2024-11-04"
  }
}
```

---

## 🗄️ Modèle de données

### Entités principales

```
User (abstract)
├── Manager (WAREHOUSE_MANAGER)
│   └── warehouseId
├── Client (CLIENT)
│   ├── name
│   └── contact
└── Admin (ADMIN)

Product
├── id (UUID)
├── sku (unique)
├── name
├── description
├── price
├── active
└── inventory → List<Inventory>

Warehouse
├── id (UUID)
├── name
├── location
├── active
└── inventory → List<Inventory>

Inventory
├── id (UUID)
├── product → Product
├── warehouse → Warehouse
├── quantity
├── reorderLevel
└── maxStock

SalesOrder / PurchaseOrder
├── id (UUID)
├── orderDate
├── status
├── totalAmount
└── orderLines → List<OrderLine>

Shipment
├── id (UUID)
├── trackingNumber
├── status (PENDING, IN_TRANSIT, DELIVERED, CANCELLED)
├── shippedAt
├── deliveredAt
├── carrier → Carrier
├── warehouse → Warehouse
└── order → SalesOrder

InventoryMovement
├── id (UUID)
├── movementDate
├── movementType (IN, OUT)
├── quantity
├── product → Product
└── warehouse → Warehouse
```

**Stratégie d'héritage :** `JOINED` pour la hiérarchie `User`

**Relations :**
- `Manager` → `Warehouse` : ManyToOne
- `Product` ↔ `Warehouse` : ManyToMany via `Inventory`
- `Shipment` → `Carrier`, `Warehouse`, `SalesOrder` : ManyToOne
- `SalesOrder` → `Client` : ManyToOne

---

## 🔒 Sécurité

### Mécanisme JWT

1. **Authentification** : Login → JWT généré avec claims (email, rôles)
2. **Autorisation** : JWT dans header `Authorization: Bearer <token>`
3. **Validation** : Filter JWT vérifie signature & expiration
4. **Révocation** : Logout ajoute le token à une blacklist

### Endpoints publics (sans authentification)
- `/api/auth/register`
- `/api/auth/login`
- `/swagger-ui/**`
- `/v3/api-docs/**`

### Matrice de permissions

| Endpoint | ADMIN | WAREHOUSE_MANAGER | CLIENT |
|----------|-------|-------------------|--------|
| Managers CRUD | ✅ | ❌ | ❌ |
| Clients List/Create | ✅ | ✅ | ❌ |
| Products CRUD | ✅ | ✅ (lecture seule) | ❌ |
| Warehouses CRUD | ✅ | ✅ (lecture seule) | ❌ |
| Shipments | ✅ | ✅ | ❌ |
| Reports | ✅ | ✅ | ❌ |

---

## 🧪 Tests

### Exécuter les tests

```bash
# Tous les tests
./mvnw test

# Tests d'une classe spécifique
./mvnw test -Dtest=ManagerControllerTest

# Tests avec couverture (JaCoCo)
./mvnw clean test jacoco:report
# Rapport disponible dans target/site/jacoco/index.html

# Skip tests lors du build
./mvnw clean package -DskipTests
```

---

## 🚀 Déploiement

### Production avec Docker

```bash
# 1. Build l'image
docker build -t digital-logistics:latest .

# 2. Tag pour registry
docker tag digital-logistics:latest registry.example.com/digital-logistics:1.0.0

# 3. Push vers registry
docker push registry.example.com/digital-logistics:1.0.0

# 4. Déployer sur serveur
docker run -d \
  --name digital-logistics-prod \
  --restart unless-stopped \
  -p 8090:8090 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db-host:5432/logistics_prod \
  -e SPRING_DATASOURCE_USERNAME=prod_user \
  -e SPRING_DATASOURCE_PASSWORD=super_secure_password \
  -e JWT_SECRET=production_jwt_secret_min_32_chars \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=validate \
  registry.example.com/digital-logistics:1.0.0
```

---

## 🔧 Troubleshooting

### Problème : Port déjà utilisé

```bash
# Trouver le processus utilisant le port 8090
lsof -i :8090
# ou
netstat -ano | findstr :8090

# Tuer le processus
kill -9 <PID>
```

### Problème : Erreur de connexion PostgreSQL

**Symptôme :** `Connection refused` ou `Authentication failed`

**Solutions :**
1. Vérifier que PostgreSQL est démarré : `docker ps` ou `systemctl status postgresql`
2. Vérifier les credentials dans `application.yml` ou variables d'environnement
3. Vérifier la base de données existe : `psql -U postgres -c "\l"`
4. Tester la connexion : `psql -h localhost -U postgres -d digital_logistics`

### Problème : Swagger UI ne charge pas

**Solutions :**
1. Vérifier l'URL : http://localhost:8090/swagger-ui/index.html (pas swagger-ui.html)
2. Vérifier la configuration dans `application.yml`
3. Vérifier les logs pour erreurs SpringDoc
4. Tester `/v3/api-docs` : http://localhost:8090/v3/api-docs

### Problème : Docker build échoue

**Symptôme :** `mvnw: Permission denied`

**Solution :**
```bash
# Dans le Dockerfile, ajouter :
RUN chmod +x /app/mvnw

# Ou localement :
chmod +x mvnw
git update-index --chmod=+x mvnw
```

### Logs et debugging

```bash
# Logs Docker Compose
docker-compose logs -f app

# Logs container spécifique
docker logs -f <container-id>

# Entrer dans le container
docker exec -it digital-logistics-app sh

# Vérifier la connectivité DB depuis le container
docker exec -it digital-logistics-app sh -c "nc -zv postgres 5432"
```

---

## 📝 Notes de version

### Version 1.0.0 (2024-11-04)

**Fonctionnalités initiales :**
- ✅ Authentification JWT complète
- ✅ CRUD Managers (ADMIN uniquement)
- ✅ CRUD Clients
- ✅ CRUD Produits avec pagination
- ✅ CRUD Entrepôts
- ✅ Gestion des expéditions
- ✅ 4 rapports statistiques (commandes, inventaire, expéditions, mouvements)
- ✅ Documentation Swagger UI
- ✅ Containerisation Docker
- ✅ Docker Compose avec PostgreSQL et pgAdmin

**Améliorations futures :**
- [ ] Migration Flyway/Liquibase
- [ ] Cache Redis pour performances
- [ ] Monitoring avec Actuator + Prometheus
- [ ] Notifications par email/SMS
- [ ] Export de rapports PDF/Excel
- [ ] API GraphQL en complément REST
- [ ] Tests de charge (JMeter/Gatling)
- [ ] CI/CD avec GitHub Actions

---

**Made with ❤️ by Digital Logistics Team**
