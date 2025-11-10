# Digital Logistics API# Digital Logistics API# Digital Logistics — README



[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)

[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)Bref: API Spring Boot pour la gestion logistique (PostgreSQL). Ce dépôt contient les entités JPA, repositories Spring Data, services et controllers minimalistes. Le projet est prévu pour être exécuté en local via Maven ou via Docker Compose (Postgres + pgAdmin + app).

[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)



> **API REST complète pour la gestion logistique** — Gestion d'entrepôts, produits, commandes, expéditions, inventaire et rapports statistiques avec authentification JWT et documentation Swagger.[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)## Contenu principal



---[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)



## 📋 Table des matières[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)- `pom.xml` — configuration Maven (Spring Boot 3.5.x, Java 17, MapStruct, Lombok)



- [À propos](#-à-propos)- `Dockerfile` — Dockerfile de développement (lance `mvn spring-boot:run` pour live-reload)

- [Fonctionnalités](#-fonctionnalités)

- [Architecture](#-architecture)> **API REST complète pour la gestion logistique** — Gestion d'entrepôts, produits, commandes, expéditions, inventaire et rapports statistiques avec authentification JWT et documentation Swagger.- `docker-compose.yaml` — services: `postgres`, `pgadmin`, `app` (monorepo image)

- [Stack technologique](#-stack-technologique)

- [Prérequis](#-prérequis)- `src/main/java/com/example/digitallogistics` — code source Java

- [Installation](#-installation)

- [Configuration](#-configuration)---  - `LogisticsApiApplication.java` — point d'entrée Spring Boot

- [Utilisation](#-utilisation)

- [Endpoints API](#-endpoints-api)  - `controller/` — controllers REST (ex: `UserController`, `TestController`)

- [Modèle de données](#-modèle-de-données)

- [Sécurité](#-sécurité)## 📋 Table des matières  - `model/` — `entity/`, `dto/`, `enums/`, `mapper/`

- [Tests](#-tests)

- [Déploiement](#-déploiement)  - `repository/` — Spring Data JPA repositories

- [Troubleshooting](#-troubleshooting)

- [À propos](#-à-propos)  - `service/` — services métiers

---

- [Fonctionnalités](#-fonctionnalités)  - `config/`, `exception/`, `jobs/`, `util/`

## 🚀 À propos

- [Architecture](#-architecture)- `src/main/resources/application.yml` — configuration Spring Boot

**Digital Logistics** est une API RESTful moderne construite avec Spring Boot 3.5.7, conçue pour gérer l'ensemble des opérations logistiques d'une entreprise :

- [Stack technologique](#-stack-technologique)- `src/main/resources/data.sql` — script d'initialisation (DDL + INSERTs) monté dans Postgres container

- **Gestion des utilisateurs** : Administrateurs, managers d'entrepôt, clients avec rôles distincts

- **Gestion d'inventaire** : Suivi des produits, stocks, mouvements d'inventaire- [Prérequis](#-prérequis)

- **Gestion des commandes** : Commandes d'achat, commandes de vente avec réservation automatique de stock

- **Gestion des expéditions** : Suivi des livraisons, transporteurs, statuts d'expédition- [Installation](#-installation)## Structure de fichiers (extraits)

- **Rapports statistiques** : Analyse des commandes, inventaire, expéditions, mouvements

- **Sécurité JWT** : Authentification robuste avec autorisation basée sur les rôles- [Configuration](#-configuration)



---- [Utilisation](#-utilisation)```



## ✨ Fonctionnalités- [Endpoints API](#-endpoints-api)docker-compose.yaml



### 🔐 Authentification & Autorisation- [Modèle de données](#-modèle-de-données)Dockerfile

- JWT (JSON Web Token) pour l'authentification stateless

- 3 rôles utilisateurs : `ADMIN`, `WAREHOUSE_MANAGER`, `CLIENT`- [Sécurité](#-sécurité)pom.xml

- Autorisation granulaire par endpoint avec `@PreAuthorize`

- Gestion de révocation de tokens- [Tests](#-tests)src/

- Enregistrement de nouveaux clients

- Hachage sécurisé des mots de passe (BCrypt)- [Déploiement](#-déploiement)  main/



### 👥 Gestion des utilisateurs- [Troubleshooting](#-troubleshooting)    java/com/example/digitallogistics/

- **Managers** (CRUD complet, ADMIN uniquement)

  - Création, lecture, mise à jour, suppression de managers      controller/

  - Filtrage par entrepôt et statut actif

  - Validation d'unicité des emails---        UserController.java

- **Clients** (CRUD complet)

  - Liste, création, détails, mise à jour de clients        TestController.java

  - Gestion des informations de contact

  - Accès sécurisé par rôle## 🚀 À propos      model/



### 📦 Gestion des produits        entity/

- CRUD complet des produits

- Pagination et filtrage (recherche, statut actif)**Digital Logistics** est une API RESTful moderne construite avec Spring Boot 3.5.7, conçue pour gérer l'ensemble des opérations logistiques d'une entreprise :        enums/

- Recherche par SKU

- Gestion des stocks par entrepôt        dto/



### 🏢 Gestion des entrepôts- **Gestion des utilisateurs** : Administrateurs, managers d'entrepôt, clients avec rôles distincts      repository/

- CRUD des entrepôts

- Gestion des localisations- **Gestion d'inventaire** : Suivi des produits, stocks, mouvements d'inventaire      service/

- Suivi de capacité et inventaire

- **Gestion des commandes** : Commandes d'achat, commandes de vente avec lignes de commande    resources/

### 📊 Gestion de l'inventaire

- Suivi en temps réel des stocks (quantité disponible, quantité réservée)- **Gestion des expéditions** : Suivi des livraisons, transporteurs, statuts d'expédition      application.yml

- Réservation automatique lors de la création de commandes

- Mouvements d'inventaire (INBOUND, OUTBOUND, ADJUSTMENT)- **Rapports statistiques** : Analyse des commandes, inventaire, expéditions, mouvements      data.sql

- Alertes de réapprovisionnement

- Mise à jour automatique lors de la réception des commandes fournisseurs- **Sécurité JWT** : Authentification robuste avec autorisation basée sur les rôles```



### 🛒 Gestion des commandes

- **Commandes de vente** (Sales Orders)

  - Création avec validation de disponibilité---## Endpoints importants (User)

  - Réservation automatique d'inventaire (quantité <= stock disponible)

  - Statuts : CREATED, RESERVED, SHIPPED, DELIVERED, CANCELLEDBase path: `/api/users`

  - Suivi des lignes de commande

- **Commandes fournisseurs** (Purchase Orders)## ✨ Fonctionnalités

  - Création de bons de commande

  - Approbation des commandes- GET `/api/users` — liste tous les users (200 OK)

  - Réception avec mise à jour automatique des stocks

  - Annulation de commandes### 🔐 Authentification & Autorisation- GET `/api/users/{id}` — récupère user par UUID (200 / 404)

  - Statuts : CREATED, APPROVED, RECEIVED, CANCELLED

- JWT (JSON Web Token) pour l'authentification stateless- GET `/api/users/by-email?email=...` — récupère par email (200 / 404)

### 📈 Mouvements de stock

- Enregistrement des mouvements INBOUND (entrée)- 3 rôles utilisateurs : `ADMIN`, `WAREHOUSE_MANAGER`, `CLIENT`- GET `/api/users/role/{role}` — récupère users par rôle (200, peut être vide)

- Enregistrement des mouvements OUTBOUND (sortie)

- Ajustements d'inventaire (ADJUSTMENT)- Autorisation granulaire par endpoint avec `@PreAuthorize`- POST `/api/users` — crée un user (201 Created + Location header)

- Mise à jour automatique des quantités en stock

- Traçabilité complète avec référence et description- Gestion de révocation de tokens- PUT `/api/users/{id}` — met à jour un user (200 / 404)



### 📊 Rapports statistiques- Enregistrement de nouveaux clients- DELETE `/api/users/{id}` — supprime (204 No Content)

- **Rapport de commandes** : Taux de livraison, backorders, revenus totaux

- **Rapport d'inventaire** : État des stocks, ruptures, surstocks, rotation- Hachage sécurisé des mots de passe (BCrypt)

- **Rapport d'expéditions** : Performance par transporteur, livraison à temps

- **Rapport de mouvements** : Analyse des entrées/sorties d'inventaireExemples (adapter le port et UUID):

- Filtrage par dates et entrepôts

### 👥 Gestion des utilisateurs

### 🚚 Gestion des expéditions

- CRUD des expéditions- **Managers** (CRUD complet, ADMIN uniquement)```bash

- Suivi des statuts (PENDING, IN_TRANSIT, DELIVERED, CANCELLED)

- Pagination et filtrage avancé  - Création, lecture, mise à jour, suppression de managers# lister

- Association avec transporteurs et commandes

  - Filtrage par entrepôt et statut actifcurl -i http://localhost:8080/api/users

### 📄 Documentation interactive

- Interface Swagger UI intégrée  - Validation d'unicité des emails

- Documentation OpenAPI 3.0

- Test des endpoints directement depuis le navigateur- **Clients** (CRUD complet)# récupérer par id



---  - Liste, création, détails, mise à jour de clientscurl -i http://localhost:8080/api/users/123e4567-e89b-12d3-a456-426614174000



## 🏗️ Architecture  - Gestion des informations de contact



```  - Accès sécurisé par rôle# récupérer par email

digital-logistics/

├── src/curl -i "http://localhost:8080/api/users/by-email?email=someone@example.com"

│   ├── main/

│   │   ├── java/com/example/digitallogistics/### 📦 Gestion des produits

│   │   │   ├── LogisticsApiApplication.java    # Point d'entrée Spring Boot

│   │   │   ├── config/                          # Configuration (Security, Swagger, etc.)- CRUD complet des produits# récupérer par role

│   │   │   │   ├── SecurityConfig.java         # Configuration JWT & Spring Security

│   │   │   │   ├── SwaggerConfig.java          # Configuration OpenAPI/Swagger- Pagination et filtrage (recherche, statut actif)curl -i http://localhost:8080/api/users/role/ADMIN

│   │   │   │   └── ...

│   │   │   ├── controller/                      # Controllers REST- Recherche par SKU

│   │   │   │   ├── AuthController.java         # Authentification (login, register, logout)

│   │   │   │   ├── ManagerController.java      # CRUD Managers (ADMIN only)- Gestion des stocks par entrepôt# créer

│   │   │   │   ├── ClientController.java       # CRUD Clients

│   │   │   │   ├── ProductController.java      # CRUD Produitscurl -i -H 'Content-Type: application/json' -d '{"id":"<uuid>","email":"a@b.com","password":"pass","role":"ADMIN","active":true}' http://localhost:8080/api/users

│   │   │   │   ├── WarehouseController.java    # CRUD Entrepôts

│   │   │   │   ├── InventoryController.java    # Gestion inventaire### 🏢 Gestion des entrepôts

│   │   │   │   ├── InventoryMovementController.java # Mouvements de stock

│   │   │   │   ├── SalesOrderController.java   # Commandes de vente- CRUD des entrepôts```

│   │   │   │   ├── PurchaseOrderController.java # Commandes fournisseurs

│   │   │   │   ├── SupplierController.java     # Gestion fournisseurs- Gestion des localisations

│   │   │   │   ├── CarrierController.java      # Gestion transporteurs

│   │   │   │   ├── ShipmentController.java     # Gestion expéditions- Suivi de capacité et inventaire> Remarque: le mapping pour le rôle est `/role/{role}` (évite l'ambiguïté avec `/{id}`).

│   │   │   │   ├── ReportController.java       # Rapports statistiques

│   │   │   │   └── ...

│   │   │   ├── model/

│   │   │   │   ├── entity/                     # Entités JPA### 📊 Rapports statistiques## Build & exécution

│   │   │   │   │   ├── User.java              # Classe abstraite (héritage JOINED)

│   │   │   │   │   ├── Manager.java           # Manager extends User- **Rapport de commandes** : Taux de livraison, backorders, revenus totaux

│   │   │   │   │   ├── Client.java            # Client extends User

│   │   │   │   │   ├── Product.java- **Rapport d'inventaire** : État des stocks, ruptures, surstocks, rotation1) En local (Maven)

│   │   │   │   │   ├── Warehouse.java

│   │   │   │   │   ├── Inventory.java- **Rapport d'expéditions** : Performance par transporteur, livraison à temps

│   │   │   │   │   ├── SalesOrder.java

│   │   │   │   │   ├── SalesOrderLine.java- **Rapport de mouvements** : Analyse des entrées/sorties d'inventaire```bash

│   │   │   │   │   ├── PurchaseOrder.java

│   │   │   │   │   ├── PurchaseOrderLine.java- Filtrage par dates et entrepôts# compiler et packager

│   │   │   │   │   ├── InventoryMovement.java

│   │   │   │   │   ├── Supplier.java./mvnw -DskipTests package

│   │   │   │   │   ├── Carrier.java

│   │   │   │   │   ├── Shipment.java### 🚚 Gestion des expéditions

│   │   │   │   │   └── ...

│   │   │   │   ├── dto/                        # Data Transfer Objects- CRUD des expéditions# exécuter localement (jar)

│   │   │   │   ├── enums/                      # Énumérations (Role, Status, etc.)

│   │   │   │   └── mapper/                     # Mappers DTO <-> Entity (MapStruct)- Suivi des statuts (PENDING, IN_TRANSIT, DELIVERED, CANCELLED)java -jar target/digital-logistics-1.0.0.jar

│   │   │   ├── repository/                      # Spring Data JPA Repositories

│   │   │   ├── service/                         # Services métiers (interfaces + implémentations)- Pagination et filtrage avancé```

│   │   │   ├── exception/                       # Gestion des exceptions

│   │   │   ├── util/                            # Utilitaires (JWT, etc.)- Association avec transporteurs et commandes

│   │   │   └── jobs/                            # Tâches planifiées (optionnel)

│   │   └── resources/2) Avec Docker Compose (développement)

│   │       ├── application.yml                  # Configuration Spring Boot

│   │       └── data.sql                         # Script d'initialisation DB (optionnel)### 📄 Documentation interactive

│   └── test/                                     # Tests unitaires et d'intégration

├── target/                                       # Artefacts de build Maven- Interface Swagger UI intégrée- Copier `.env.example` en `.env` et ajuster les variables (`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `SERVER_PORT`, ...)

├── Dockerfile                                    # Image Docker multi-stage

├── docker-compose.yaml                           # Orchestration Docker (PostgreSQL, pgAdmin, app)- Documentation OpenAPI 3.0- Démarrer les services:

├── pom.xml                                       # Configuration Maven

└── README.md                                     # Ce fichier- Test des endpoints directement depuis le navigateur

```

```bash

**Principes architecturaux :**

- **Architecture en couches** : Controller → Service → Repository → Database---docker compose up --build -d

- **Séparation des préoccupations** : DTOs pour l'API, Entities pour la persistance

- **Injection de dépendances** : Spring IoC/DI```

- **Héritage JPA** : Strategy JOINED pour User/Manager/Client

- **Sécurité** : Filter chain JWT avant UsernamePasswordAuthenticationFilter## 🏗️ Architecture



---- Par défaut le service `app` lit `SERVER_PORT` (défini dans `.env`). Vérifiez le port exposé dans la sortie de `docker compose ps`.



## 🛠️ Stack technologique```



| Composant | Technologie | Version |digital-logistics/Notes importantes:

|-----------|-------------|---------|

| **Framework** | Spring Boot | 3.5.7 |├── src/- Le `Dockerfile` fourni est orienté développement: il exécute `mvn spring-boot:run` et inclut `spring-boot-devtools`. Cela provoque parfois des redémarrages (devtools) et des réponses vides lors des rebuilds. Pour un container stable (production-like), préférez changer le Dockerfile pour exécuter le jar repackagé : `java -jar target/*.jar`.

| **Langage** | Java | 17 |

| **Base de données** | PostgreSQL | 16 (Alpine) |│   ├── main/

| **Build Tool** | Maven | 3.9+ |

| **Mapping DTO** | MapStruct | 1.6.2 |│   │   ├── java/com/example/digitallogistics/Exemple de Dockerfile (extrait recommandé pour build d'image finale):

| **Boilerplate** | Lombok | 1.18.34 |

| **ORM** | Hibernate | 6.6.x |│   │   │   ├── LogisticsApiApplication.java    # Point d'entrée Spring Boot

| **Sécurité** | Spring Security + JWT | jjwt 0.11.5 |

| **Documentation** | SpringDoc OpenAPI | 2.8.0 |│   │   │   ├── config/                          # Configuration (Security, Swagger, etc.)```dockerfile

| **Pool de connexions** | HikariCP | Intégré |

| **Validation** | Hibernate Validator | Intégré |│   │   │   │   ├── SecurityConfig.java         # Configuration JWT & Spring SecurityFROM eclipse-temurin:17-jdk-alpine

| **Conteneurisation** | Docker + Docker Compose | Latest |

| **Base Image** | Eclipse Temurin | 17-JDK |│   │   │   │   ├── SwaggerConfig.java          # Configuration OpenAPI/SwaggerWORKDIR /app



**Dépendances clés :**│   │   │   │   └── ...COPY target/digital-logistics-1.0.0.jar app.jar

```xml

<dependencies>│   │   │   ├── controller/                      # Controllers RESTEXPOSE 8080

    <dependency>

        <groupId>org.springframework.boot</groupId>│   │   │   │   ├── AuthController.java         # Authentification (login, register, logout)ENTRYPOINT ["java","-jar","/app/app.jar"]

        <artifactId>spring-boot-starter-web</artifactId>

    </dependency>│   │   │   │   ├── ManagerController.java      # CRUD Managers (ADMIN only)```

    <dependency>

        <groupId>org.springframework.boot</groupId>│   │   │   │   ├── ClientController.java       # CRUD Clients

        <artifactId>spring-boot-starter-data-jpa</artifactId>

    </dependency>│   │   │   │   ├── ProductController.java      # CRUD Produits3) Re-créer la DB seed

    <dependency>

        <groupId>org.springframework.boot</groupId>│   │   │   │   ├── WarehouseController.java    # CRUD Entrepôts

        <artifactId>spring-boot-starter-security</artifactId>

    </dependency>│   │   │   │   ├── ShipmentController.java     # Gestion expéditions- Le script `src/main/resources/data.sql` est monté dans `/docker-entrypoint-initdb.d/` du container Postgres. Il ne s'exécute que lors de l'initialisation d'un volume neuf. Pour ré-appliquer le seed, supprimez le volume `pg_data` puis relancez `docker compose up`:

    <dependency>

        <groupId>org.postgresql</groupId>│   │   │   │   ├── ReportController.java       # Rapports statistiques

        <artifactId>postgresql</artifactId>

        <version>42.7.3</version>│   │   │   │   └── ...```bash

    </dependency>

    <dependency>│   │   │   ├── model/docker compose down

        <groupId>io.jsonwebtoken</groupId>

        <artifactId>jjwt-api</artifactId>│   │   │   │   ├── entity/                     # Entités JPAdocker volume rm <project>_pg_data  # ou `docker volume ls` pour trouver le nom

        <version>0.11.5</version>

    </dependency>│   │   │   │   │   ├── User.java              # Classe abstraite (héritage JOINED)docker compose up --build

    <dependency>

        <groupId>org.springdoc</groupId>│   │   │   │   │   ├── Manager.java           # Manager extends User```

        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>

        <version>2.8.0</version>│   │   │   │   │   ├── Client.java            # Client extends User

    </dependency>

    <dependency>│   │   │   │   │   ├── Product.java## Debugging courant

        <groupId>org.mapstruct</groupId>

        <artifactId>mapstruct</artifactId>│   │   │   │   │   ├── Warehouse.java

        <version>1.6.2</version>

    </dependency>│   │   │   │   │   ├── Inventory.java- `No static resource api/users.`: cela signifie qu'aucun controller ne correspond au chemin demandé — souvent dû à une route mal mappée ou au controller non chargé. Vérifiez les logs (`docker compose logs app --tail 200`) ; cherchez `Tomcat started` et `Started LogisticsApiApplication`.

    <dependency>

        <groupId>org.projectlombok</groupId>│   │   │   │   │   ├── SalesOrder.java- `Empty reply from server` pendant les rebuilds: fréquent si devtools/maven-run à l'intérieur du container redémarre le processus. Solution: lancer le jar directement pour stabilité.

        <artifactId>lombok</artifactId>

        <version>1.18.34</version>│   │   │   │   │   ├── Shipment.java

    </dependency>

</dependencies>│   │   │   │   │   └── ...## Tests

```

│   │   │   │   ├── dto/                        # Data Transfer Objects

---

│   │   │   │   ├── enums/                      # Énumérations (Role, Status, etc.)- Les dépendances de test sont configurées (`spring-boot-starter-test`). Les tests unitaires se trouvent sous `src/test`. Pour lancer les tests:

## 📋 Prérequis

│   │   │   │   └── mapper/                     # Mappers DTO <-> Entity (MapStruct + manuels)

### Pour Docker (Recommandé)

- **Docker** : Version 20.10+│   │   │   ├── repository/                      # Spring Data JPA Repositories```bash

- **Docker Compose** : Version 2.0+

│   │   │   ├── service/                         # Services métiers (interfaces + implémentations)./mvnw test

### Pour exécution locale

- **Java JDK** : Version 17 ou supérieure│   │   │   ├── exception/                       # Gestion des exceptions```

- **Maven** : Version 3.9+ (ou utiliser `./mvnw` inclus)

- **PostgreSQL** : Version 14+ (serveur local)│   │   │   ├── util/                            # Utilitaires (JWT, etc.)



---│   │   │   └── jobs/                            # Tâches planifiées (optionnel)## Prochaines améliorations suggérées



## 📦 Installation│   │   └── resources/



### Méthode 1 : Docker Compose (Recommandé)│   │       ├── application.yml                  # Configuration Spring Boot- Convertir `data.sql` en migrations Flyway/Liquibase pour un contrôle plus sûr des migrations.



```bash│   │       └── data.sql                         # Script d'initialisation DB (optionnel)- Remplacer l'exécution `mvn spring-boot:run` dans Docker par le jar packagé pour stabilité en conteneur.

# 1. Cloner le repository

git clone https://github.com/mohammedennaim/digital-logistics.git│   └── test/                                     # Tests unitaires et d'intégration- Ajouter DTOs + MapStruct mappers pour séparer entités et API.

cd digital-logistics

├── target/                                       # Artefacts de build Maven- Ajouter authentification / hashing des mots de passe avant stockage.

# 2. Créer le fichier .env (optionnel, valeurs par défaut disponibles)

cat > .env << EOF├── Dockerfile                                    # Image Docker multi-stage

# PostgreSQL

POSTGRES_USER=postgres├── docker-compose.yaml                           # Orchestration Docker (PostgreSQL, pgAdmin, app)## Contacts / références

POSTGRES_PASSWORD=password

POSTGRES_DB=digital_logistics├── pom.xml                                       # Configuration Maven

POSTGRES_PORT=5432

└── README.md                                     # Ce fichier- Point d'entrée: `src/main/java/com/example/digitallogistics/LogisticsApiApplication.java`

# pgAdmin

PGADMIN_DEFAULT_EMAIL=admin@example.com```- Fichier d'initialisation DB: `src/main/resources/data.sql`

PGADMIN_DEFAULT_PASSWORD=admin

PGADMIN_PORT=5050



# Application**Principes architecturaux :**---

SERVER_PORT=8090

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/digital_logistics- **Architecture en couches** : Controller → Service → Repository → Database

SPRING_DATASOURCE_USERNAME=postgres

SPRING_DATASOURCE_PASSWORD=password- **Séparation des préoccupations** : DTOs pour l'API, Entities pour la persistanceSi vous voulez, je peux :

SPRING_JPA_HIBERNATE_DDL_AUTO=update

SPRING_JPA_SHOW_SQL=false- **Injection de dépendances** : Spring IoC/DI- générer automatiquement un `README.md` plus détaillé avec tous les endpoints (tous les controllers), ou

EOF

- **Héritage JPA** : Strategy JOINED pour User/Manager/Client- modifier le Dockerfile pour produire une image basée sur le jar (stable) et adapter `docker-compose.yaml` (je peux appliquer ce changement et redéployer).

# 3. Démarrer tous les services

docker-compose up --build -d- **Sécurité** : Filter chain JWT avant UsernamePasswordAuthenticationFilter



# 4. Vérifier l'état des servicesDites-moi quelle option vous préférez (générer plus de docs OU adapter Docker pour exécuter le jar). 

docker-compose ps---



# 5. Voir les logs de l'application## 🛠️ Stack technologique

docker-compose logs -f app

```| Composant | Technologie | Version |

|-----------|-------------|---------|

**Services disponibles :**| **Framework** | Spring Boot | 3.5.7 |

- **Application** : http://localhost:8090| **Langage** | Java | 17 |

- **Swagger UI** : http://localhost:8090/swagger-ui/index.html| **Base de données** | PostgreSQL | 16 (Alpine) |

- **pgAdmin** : http://localhost:5050| **Build Tool** | Maven | 3.9.5 |

| **Mapping DTO** | MapStruct | 1.6.2 |

### Méthode 2 : Exécution locale avec Maven| **Boilerplate** | Lombok | 1.18.34 |

| **ORM** | Hibernate | 6.6.33.Final |

```bash| **Sécurité** | Spring Security + JWT | 3.5.7 |

# 1. Cloner le repository| **Documentation** | SpringDoc OpenAPI | 2.7.0 |

git clone https://github.com/mohammedennaim/digital-logistics.git| **Pool de connexions** | HikariCP | Intégré |

cd digital-logistics| **Validation** | Hibernate Validator | Intégré |

| **Conteneurisation** | Docker + Docker Compose | Latest |

# 2. Configurer PostgreSQL local| **Base Image** | Eclipse Temurin | 17-JDK/JRE |

createdb digital_logistics

# Ou via psql:**Dépendances clés :**

# psql -U postgres -c "CREATE DATABASE digital_logistics;"```xml

<dependencies>

# 3. Configurer les variables d'environnement (optionnel)    <dependency>

export DB_URL=jdbc:postgresql://localhost:5432/digital_logistics        <groupId>org.springframework.boot</groupId>

export DB_USERNAME=postgres        <artifactId>spring-boot-starter-web</artifactId>

export DB_PASSWORD=your_password    </dependency>

    <dependency>

# 4. Build du projet        <groupId>org.springframework.boot</groupId>

./mvnw clean package -DskipTests        <artifactId>spring-boot-starter-data-jpa</artifactId>

    </dependency>

# 5. Exécuter l'application    <dependency>

java -jar target/digital-logistics-1.0.0.jar        <groupId>org.springframework.boot</groupId>

        <artifactId>spring-boot-starter-security</artifactId>

# Ou directement avec Maven    </dependency>

./mvnw spring-boot:run    <dependency>

```        <groupId>org.springframework.boot</groupId>

        <artifactId>spring-boot-starter-validation</artifactId>

---    </dependency>

    <dependency>

## ⚙️ Configuration        <groupId>org.postgresql</groupId>

        <artifactId>postgresql</artifactId>

### Variables d'environnement    </dependency>

    <dependency>

| Variable | Description | Valeur par défaut |        <groupId>io.jsonwebtoken</groupId>

|----------|-------------|-------------------|        <artifactId>jjwt-api</artifactId>

| `SERVER_PORT` | Port d'écoute de l'application | `8090` |        <version>0.12.6</version>

| `DB_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/digital_logistics` |    </dependency>

| `DB_USERNAME` | Utilisateur PostgreSQL | `postgres` |    <dependency>

| `DB_PASSWORD` | Mot de passe PostgreSQL | `postgres` |        <groupId>org.springdoc</groupId>

| `JWT_SECRET` | Clé secrète JWT (min 32 caractères) | `YourVerySecure...` |        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>

| `JWT_EXPIRATION` | Durée de validité du token (ms) | `3600000` (1h) |        <version>2.7.0</version>

| `SHOW_SQL` | Afficher les requêtes SQL | `false` |    </dependency>

</dependencies>

### Fichier application.yml```



```yaml---

spring:

  application:## 📋 Prérequis

    name: digital-logistics

  ### Pour Docker (Recommandé)

  datasource:- **Docker** : Version 20.10+

    url: ${DB_URL:jdbc:postgresql://localhost:5432/digital_logistics}- **Docker Compose** : Version 2.0+

    username: ${DB_USERNAME:postgres}

    password: ${DB_PASSWORD:postgres}### Pour exécution locale

  - **Java JDK** : Version 17 ou supérieure

  jpa:- **Maven** : Version 3.9+ (ou utiliser `./mvnw` inclus)

    hibernate:- **PostgreSQL** : Version 14+ (serveur local)

      ddl-auto: update  # Utiliser 'validate' en production

    show-sql: false---



app:## 📦 Installation

  jwt:

    secret: ${JWT_SECRET:YourVerySecureRandomSecretKeyHere}### Docker (Recommandé)

    expiration-ms: ${JWT_EXPIRATION:3600000}

#### Méthode 1 : Build et exécution simple

server:

  port: ${SERVER_PORT:8090}```bash

# 1. Cloner le repository

springdoc:git clone https://github.com/votre-username/digital-logistics.git

  api-docs:cd digital-logistics

    enabled: true

  swagger-ui:# 2. Build de l'image Docker

    enabled: truedocker build -t digital-logistics-app .

    path: /swagger-ui.html

```# 3. Lancer PostgreSQL

docker run -d \

**⚠️ Sécurité en production :**  --name postgres-logistics \

- Changer `JWT_SECRET` avec une valeur forte (min. 32 caractères aléatoires)  -e POSTGRES_USER=postgres \

- Utiliser `ddl-auto: validate` au lieu de `update`  -e POSTGRES_PASSWORD=password \

- Désactiver `show-sql`  -e POSTGRES_DB=digital_logistics \

- Utiliser HTTPS  -p 5432:5432 \

- Configurer CORS si nécessaire  postgres:16-alpine



---# 4. Lancer l'application

docker run -d \

## 🎯 Utilisation  --name digital-logistics-app \

  -p 8090:8090 \

### API Documentation (Swagger)  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/digital_logistics \

  -e SPRING_DATASOURCE_USERNAME=postgres \

Accéder à l'interface Swagger UI pour tester les endpoints :  -e SPRING_DATASOURCE_PASSWORD=password \

  digital-logistics-app

**URL :** http://localhost:8090/swagger-ui/index.html

# 5. Vérifier les logs

**OpenAPI JSON :** http://localhost:8090/v3/api-docsdocker logs -f digital-logistics-app

```

### Authentification

#### Méthode 2 : Docker Compose (Multi-services)

#### 1. Créer un compte (Register)

```bash

```bash# 1. Créer le fichier .env

POST /api/auth/registercat > .env << EOF

Content-Type: application/json# PostgreSQL

POSTGRES_USER=postgres

{POSTGRES_PASSWORD=password

  "name": "John Doe",POSTGRES_DB=digital_logistics

  "email": "john@example.com",POSTGRES_PORT=5432

  "password": "SecurePass123!",

  "contact": "+1234567890"# pgAdmin

}PGADMIN_DEFAULT_EMAIL=admin@example.com

```PGADMIN_DEFAULT_PASSWORD=admin

PGADMIN_PORT=5050

**Réponse :**

```json# Application

{SERVER_PORT=8090

  "id": "uuid-here",SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/digital_logistics

  "name": "John Doe",SPRING_DATASOURCE_USERNAME=postgres

  "email": "john@example.com",SPRING_DATASOURCE_PASSWORD=password

  "role": "CLIENT",SPRING_JPA_HIBERNATE_DDL_AUTO=update

  "active": trueSPRING_JPA_SHOW_SQL=false

}EOF

```

# 2. Démarrer tous les services

#### 2. Se connecter (Login)docker-compose up --build -d



```bash# 3. Vérifier l'état

POST /api/auth/logindocker-compose ps

Content-Type: application/json

# 4. Voir les logs

{docker-compose logs -f app

  "email": "john@example.com",```

  "password": "SecurePass123!"

}**Services disponibles :**

```- **Application** : http://localhost:8090

- **Swagger UI** : http://localhost:8090/swagger-ui/index.html

**Réponse :**- **pgAdmin** : http://localhost:5050

```json

{### Local (Maven)

  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

}```bash

```# 1. Cloner le repository

git clone https://github.com/votre-username/digital-logistics.git

#### 3. Utiliser le tokencd digital-logistics



```bash# 2. Configurer PostgreSQL local

GET /api/productscreatedb digital_logistics

Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...# Ou via psql:

```# psql -U postgres -c "CREATE DATABASE digital_logistics;"



---# 3. Configurer les variables d'environnement (optionnel)

export DB_URL=jdbc:postgresql://localhost:5432/digital_logistics

## 📡 Endpoints APIexport DB_USERNAME=postgres

export DB_PASSWORD=your_password

### 🔐 Authentification (`/api/auth`)

# 4. Build du projet

| Méthode | Endpoint | Description | Rôles |./mvnw clean package -DskipTests

|---------|----------|-------------|-------|

| POST | `/api/auth/register` | Créer un nouveau client | Public |# 5. Exécuter l'application

| POST | `/api/auth/login` | Se connecter (obtenir JWT) | Public |java -jar target/digital-logistics-1.0.0.jar

| POST | `/api/auth/logout` | Révoquer un token | Authentifié |

# Ou directement avec Maven

---./mvnw spring-boot:run

```

### 👤 Managers (`/api/managers`)

---

| Méthode | Endpoint | Description | Rôles |

|---------|----------|-------------|-------|## ⚙️ Configuration

| GET | `/api/managers` | Liste tous les managers | ADMIN |

| GET | `/api/managers/{id}` | Détails d'un manager | ADMIN |### Variables d'environnement

| GET | `/api/managers/warehouse/{warehouseId}` | Managers par entrepôt | ADMIN |

| GET | `/api/managers/active` | Managers actifs | ADMIN || Variable | Description | Valeur par défaut |

| POST | `/api/managers` | Créer un manager | ADMIN ||----------|-------------|-------------------|

| PUT | `/api/managers/{id}` | Mettre à jour un manager | ADMIN || `SERVER_PORT` | Port d'écoute de l'application | `8090` |

| DELETE | `/api/managers/{id}` | Supprimer un manager | ADMIN || `DB_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/logistics_db` |

| `DB_USERNAME` | Utilisateur PostgreSQL | `postgres` |

**Exemple de création :**| `DB_PASSWORD` | Mot de passe PostgreSQL | `postgres` |

```bash| `JWT_SECRET` | Clé secrète JWT (min 32 caractères) | `YourVerySecure...` |

POST /api/managers| `JWT_EXPIRATION` | Durée de validité du token (ms) | `3600000` (1h) |

Authorization: Bearer <admin-token>| `SHOW_SQL` | Afficher les requêtes SQL | `false` |

Content-Type: application/json

### Fichier application.yml

{

  "email": "manager@example.com",```yaml

  "password": "ManagerPass123!",spring:

  "name": "Manager Name",  application:

  "warehouseId": "warehouse-uuid-here",    name: digital-logistics

  "active": true  

}  datasource:

```    url: ${DB_URL:jdbc:postgresql://localhost:5432/logistics_db}

    username: ${DB_USERNAME:postgres}

---    password: ${DB_PASSWORD:postgres}

  

### 👥 Clients (`/api/clients`)  jpa:

    hibernate:

| Méthode | Endpoint | Description | Rôles |      ddl-auto: update  # Utiliser 'validate' en production

|---------|----------|-------------|-------|    show-sql: false

| GET | `/api/clients` | Liste tous les clients | ADMIN, WAREHOUSE_MANAGER |

| POST | `/api/clients` | Créer un client | ADMIN, WAREHOUSE_MANAGER |app:

| GET | `/api/clients/{id}` | Détails d'un client | ADMIN, WAREHOUSE_MANAGER |  jwt:

| PUT | `/api/clients/{id}` | Mettre à jour un client | ADMIN, WAREHOUSE_MANAGER |    secret: ${JWT_SECRET:YourVerySecureRandomSecretKeyHere}

    expiration-ms: ${JWT_EXPIRATION:3600000}

---

server:

### 📦 Produits (`/api/products`)  port: ${SERVER_PORT:8090}



| Méthode | Endpoint | Description | Rôles |springdoc:

|---------|----------|-------------|-------|  api-docs:

| GET | `/api/products?page=0&size=20&search=keyword&active=true` | Liste paginée/filtrée | ADMIN, WAREHOUSE_MANAGER |    enabled: true

| GET | `/api/products/{id}` | Détails d'un produit | ADMIN, WAREHOUSE_MANAGER |  swagger-ui:

| GET | `/api/products/search?sku={sku}` | Recherche par SKU | ADMIN, WAREHOUSE_MANAGER |    enabled: true

| POST | `/api/products` | Créer un produit | ADMIN, WAREHOUSE_MANAGER |    path: /swagger-ui.html

| PUT | `/api/products/{id}` | Mettre à jour un produit | ADMIN, WAREHOUSE_MANAGER |```

| PATCH | `/api/products/{id}/status` | Modifier statut (actif/inactif) | ADMIN, WAREHOUSE_MANAGER |

| DELETE | `/api/products/{id}` | Supprimer un produit | ADMIN |**⚠️ Sécurité en production :**

- Changer `JWT_SECRET` avec une valeur forte (min. 32 caractères aléatoires)

---- Utiliser `ddl-auto: validate` au lieu de `update`

- Désactiver `show-sql`

### 🏢 Entrepôts (`/api/warehouses`)- Utiliser HTTPS

- Configurer CORS si nécessaire

| Méthode | Endpoint | Description | Rôles |

|---------|----------|-------------|-------|---

| GET | `/api/warehouses` | Liste tous les entrepôts | ADMIN, WAREHOUSE_MANAGER |

| POST | `/api/warehouses` | Créer un entrepôt | ADMIN, WAREHOUSE_MANAGER |## 🎯 Utilisation

| GET | `/api/warehouses/{id}` | Détails d'un entrepôt | ADMIN, WAREHOUSE_MANAGER |

| PUT | `/api/warehouses/{id}` | Mettre à jour un entrepôt | ADMIN, WAREHOUSE_MANAGER |### API Documentation (Swagger)

| DELETE | `/api/warehouses/{id}` | Supprimer un entrepôt | ADMIN |

Accéder à l'interface Swagger UI pour tester les endpoints :

---

**URL :** http://localhost:8090/swagger-ui/index.html

### 📊 Inventaire (`/api/inventory`)

**OpenAPI JSON :** http://localhost:8090/v3/api-docs

| Méthode | Endpoint | Description | Rôles |

|---------|----------|-------------|-------|### Authentification

| GET | `/api/inventory` | Liste tout l'inventaire | ADMIN, WAREHOUSE_MANAGER |

| GET | `/api/inventory/{id}` | Détails d'un inventaire | ADMIN, WAREHOUSE_MANAGER |#### 1. Créer un compte (Register)

| GET | `/api/inventory/warehouse/{warehouseId}` | Inventaire par entrepôt | ADMIN, WAREHOUSE_MANAGER |

| GET | `/api/inventory/product/{productId}` | Inventaire par produit | ADMIN, WAREHOUSE_MANAGER |```bash

| POST | `/api/inventory` | Créer un inventaire | ADMIN, WAREHOUSE_MANAGER |POST /api/auth/register

| PUT | `/api/inventory/{id}` | Mettre à jour un inventaire | ADMIN, WAREHOUSE_MANAGER |Content-Type: application/json

| DELETE | `/api/inventory/{id}` | Supprimer un inventaire | ADMIN |

{

---  "name": "John Doe",

  "email": "john@example.com",

### 📈 Mouvements de stock (`/api/inventory-movements`)  "password": "SecurePass123!",

  "contact": "+1234567890"

| Méthode | Endpoint | Description | Rôles |}

|---------|----------|-------------|-------|```

| GET | `/api/inventory-movements?type=INBOUND` | Liste des mouvements (filtrable par type) | ADMIN, WAREHOUSE_MANAGER |

| POST | `/api/inventory-movements/inbound` | Enregistrer une entrée de stock | ADMIN, WAREHOUSE_MANAGER |**Réponse :**

| POST | `/api/inventory-movements/outbound` | Enregistrer une sortie de stock | ADMIN, WAREHOUSE_MANAGER |```json

| POST | `/api/inventory-movements/adjustment` | Enregistrer un ajustement | ADMIN, WAREHOUSE_MANAGER |{

  "id": "uuid-here",

**Types de mouvements :**  "name": "John Doe",

- `INBOUND` : Entrée de stock (ajoute à qtyOnHand)  "email": "john@example.com",

- `OUTBOUND` : Sortie de stock (soustrait de qtyOnHand)  "role": "CLIENT",

- `ADJUSTMENT` : Ajustement (peut être + ou -)  "active": true

}

**Exemple d'entrée de stock :**```

```bash

POST /api/inventory-movements/inbound#### 2. Se connecter (Login)

Authorization: Bearer <token>

Content-Type: application/json```bash

POST /api/auth/login

{Content-Type: application/json

  "warehouseId": "warehouse-uuid",

  "productId": "product-uuid",{

  "quantity": 100,  "email": "john@example.com",

  "reference": "PO-2024-001",  "password": "SecurePass123!"

  "description": "Réception commande fournisseur"}

}```

```

**Réponse :**

---```json

{

### 🛒 Commandes de vente (`/api/sales-orders`)  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

}

| Méthode | Endpoint | Description | Rôles |```

|---------|----------|-------------|-------|

| GET | `/api/sales-orders` | Liste des commandes | ADMIN, WAREHOUSE_MANAGER |#### 3. Utiliser le token

| POST | `/api/sales-orders` | Créer une commande | ADMIN, WAREHOUSE_MANAGER, CLIENT |

| GET | `/api/sales-orders/{id}` | Détails d'une commande | ADMIN, WAREHOUSE_MANAGER, CLIENT |```bash

| PUT | `/api/sales-orders/{id}` | Mettre à jour une commande | ADMIN, WAREHOUSE_MANAGER |GET /api/products

| PATCH | `/api/sales-orders/{id}/status` | Changer le statut | ADMIN, WAREHOUSE_MANAGER |Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

```

**Comportement de création :**

- Valide que la quantité demandée <= stock disponible (qtyOnHand)---

- **Réserve automatiquement** l'inventaire (décrémente qtyOnHand, incrémente qtyReserved)

- Définit le statut à `RESERVED` si réservation réussie## 📡 Endpoints API

- Retourne erreur 400 si stock insuffisant

### 🔐 Authentification (`/api/auth`)

**Statuts :** `CREATED`, `RESERVED`, `SHIPPED`, `DELIVERED`, `CANCELLED`

| Méthode | Endpoint | Description | Rôles |

---|---------|----------|-------------|-------|

| POST | `/api/auth/register` | Créer un nouveau client | Public |

### 📋 Commandes fournisseurs (`/api/purchase-orders`)| POST | `/api/auth/login` | Se connecter (obtenir JWT) | Public |

| POST | `/api/auth/logout` | Révoquer un token | Authentifié |

| Méthode | Endpoint | Description | Rôles |

|---------|----------|-------------|-------|---

| GET | `/api/purchase-orders` | Liste des bons de commande | ADMIN, WAREHOUSE_MANAGER |

| POST | `/api/purchase-orders` | Créer un bon de commande | ADMIN, WAREHOUSE_MANAGER |### 👤 Managers (`/api/managers`)

| GET | `/api/purchase-orders/{id}` | Détails d'un bon de commande | ADMIN, WAREHOUSE_MANAGER |

| POST | `/api/purchase-orders/{id}/approve` | Approuver une commande | ADMIN, WAREHOUSE_MANAGER || Méthode | Endpoint | Description | Rôles |

| POST | `/api/purchase-orders/{id}/receive` | Recevoir une commande (met à jour le stock) | ADMIN, WAREHOUSE_MANAGER ||---------|----------|-------------|-------|

| POST | `/api/purchase-orders/{id}/cancel` | Annuler une commande | ADMIN, WAREHOUSE_MANAGER || GET | `/api/managers` | Liste tous les managers | ADMIN |

| GET | `/api/managers/{id}` | Détails d'un manager | ADMIN |

**Statuts :** `CREATED`, `APPROVED`, `RECEIVED`, `CANCELLED`| GET | `/api/managers/warehouse/{warehouseId}` | Managers par entrepôt | ADMIN |

| GET | `/api/managers/active` | Managers actifs | ADMIN |

**Exemple de réception :**| POST | `/api/managers` | Créer un manager | ADMIN |

```bash| PUT | `/api/managers/{id}` | Mettre à jour un manager | ADMIN |

POST /api/purchase-orders/{id}/receive| DELETE | `/api/managers/{id}` | Supprimer un manager | ADMIN |

Authorization: Bearer <token>

Content-Type: application/json**Exemple de création :**

```bash

{POST /api/managers

  "lines": [Authorization: Bearer <admin-token>

    {Content-Type: application/json

      "lineId": "line-uuid",

      "receivedQuantity": 50{

    }  "email": "manager@example.com",

  ]  "password": "ManagerPass123!",

}  "name": "Manager Name",

```  "warehouseId": "warehouse-uuid-here",

  "active": true

---}

```

### 🏭 Fournisseurs (`/api/suppliers`)

---

| Méthode | Endpoint | Description | Rôles |

|---------|----------|-------------|-------|### 👥 Clients (`/api/clients`)

| GET | `/api/suppliers` | Liste des fournisseurs | ADMIN, WAREHOUSE_MANAGER |

| POST | `/api/suppliers` | Créer un fournisseur | ADMIN, WAREHOUSE_MANAGER || Méthode | Endpoint | Description | Rôles |

| GET | `/api/suppliers/{id}` | Détails d'un fournisseur | ADMIN, WAREHOUSE_MANAGER ||---------|----------|-------------|-------|

| PUT | `/api/suppliers/{id}` | Mettre à jour un fournisseur | ADMIN, WAREHOUSE_MANAGER || GET | `/api/clients` | Liste tous les clients | ADMIN, WAREHOUSE_MANAGER |

| DELETE | `/api/suppliers/{id}` | Supprimer un fournisseur | ADMIN || POST | `/api/clients` | Créer un client | ADMIN, WAREHOUSE_MANAGER |

| GET | `/api/clients/{id}` | Détails d'un client | ADMIN, WAREHOUSE_MANAGER, DRIVER |

---| PUT | `/api/clients/{id}` | Mettre à jour un client | ADMIN, WAREHOUSE_MANAGER |



### 🚛 Transporteurs (`/api/carriers`)---



| Méthode | Endpoint | Description | Rôles |### 📦 Produits (`/api/products`)

|---------|----------|-------------|-------|

| GET | `/api/carriers` | Liste des transporteurs | ADMIN, WAREHOUSE_MANAGER || Méthode | Endpoint | Description | Rôles |

| POST | `/api/carriers` | Créer un transporteur | ADMIN, WAREHOUSE_MANAGER ||---------|----------|-------------|-------|

| GET | `/api/carriers/{id}` | Détails d'un transporteur | ADMIN, WAREHOUSE_MANAGER || GET | `/api/products?page=0&size=20&search=keyword&active=true` | Liste paginée/filtrée | ADMIN, WAREHOUSE_MANAGER |

| PUT | `/api/carriers/{id}` | Mettre à jour un transporteur | ADMIN, WAREHOUSE_MANAGER || GET | `/api/products/{id}` | Détails d'un produit | ADMIN, WAREHOUSE_MANAGER |

| DELETE | `/api/carriers/{id}` | Supprimer un transporteur | ADMIN || GET | `/api/products/search?sku={sku}` | Recherche par SKU | ADMIN, WAREHOUSE_MANAGER |

| POST | `/api/products` | Créer un produit | ADMIN, WAREHOUSE_MANAGER |

---| PUT | `/api/products/{id}` | Mettre à jour un produit | ADMIN, WAREHOUSE_MANAGER |

| PATCH | `/api/products/{id}/status` | Modifier statut (actif/inactif) | ADMIN, WAREHOUSE_MANAGER |

### 🚚 Expéditions (`/api/shipments`)| DELETE | `/api/products/{id}` | Supprimer un produit | ADMIN |



| Méthode | Endpoint | Description | Rôles |---

|---------|----------|-------------|-------|

| GET | `/api/shipments?page=0&size=20&status=PENDING&warehouseId=uuid` | Liste paginée/filtrée | ADMIN, WAREHOUSE_MANAGER |### 🏢 Entrepôts (`/api/warehouses`)

| GET | `/api/shipments/{id}` | Détails d'une expédition | ADMIN, WAREHOUSE_MANAGER |

| POST | `/api/shipments` | Créer une expédition | ADMIN, WAREHOUSE_MANAGER || Méthode | Endpoint | Description | Rôles |

| PUT | `/api/shipments/{id}` | Mettre à jour une expédition | ADMIN, WAREHOUSE_MANAGER ||---------|----------|-------------|-------|

| PATCH | `/api/shipments/{id}/status` | Changer le statut | ADMIN, WAREHOUSE_MANAGER || GET | `/api/warehouses` | Liste tous les entrepôts | ADMIN, WAREHOUSE_MANAGER |

| DELETE | `/api/shipments/{id}` | Annuler une expédition | ADMIN || POST | `/api/warehouses` | Créer un entrepôt | ADMIN, WAREHOUSE_MANAGER |

| GET | `/api/warehouses/{id}` | Détails d'un entrepôt | ADMIN, WAREHOUSE_MANAGER |

**Statuts disponibles :** `PENDING`, `IN_TRANSIT`, `DELIVERED`, `CANCELLED`| PUT | `/api/warehouses/{id}` | Mettre à jour un entrepôt | ADMIN, WAREHOUSE_MANAGER |

| DELETE | `/api/warehouses/{id}` | Supprimer un entrepôt | ADMIN |

---

---

### 📊 Rapports (`/api/reports`)

### 🚚 Expéditions (`/api/shipments`)

| Méthode | Endpoint | Description | Rôles |

|---------|----------|-------------|-------|| Méthode | Endpoint | Description | Rôles |

| GET | `/api/reports/orders?fromDate=2024-10-01&toDate=2024-11-04` | Statistiques des commandes | ADMIN, WAREHOUSE_MANAGER ||---------|----------|-------------|-------|

| GET | `/api/reports/inventory?warehouseId=uuid` | Rapport d'inventaire | ADMIN, WAREHOUSE_MANAGER || GET | `/api/shipments?page=0&size=20&status=PENDING&warehouseId=uuid` | Liste paginée/filtrée | ADMIN, WAREHOUSE_MANAGER |

| GET | `/api/reports/shipments?carrierId=uuid&fromDate=...&toDate=...` | Performance des expéditions | ADMIN, WAREHOUSE_MANAGER || GET | `/api/shipments/{id}` | Détails d'une expédition | ADMIN, WAREHOUSE_MANAGER |

| GET | `/api/reports/movements?warehouseId=uuid&fromDate=...&toDate=...` | Mouvements d'inventaire | ADMIN, WAREHOUSE_MANAGER || POST | `/api/shipments` | Créer une expédition | ADMIN, WAREHOUSE_MANAGER |

| PUT | `/api/shipments/{id}` | Mettre à jour une expédition | ADMIN, WAREHOUSE_MANAGER |

**Exemple de rapport de commandes :**| PATCH | `/api/shipments/{id}/status` | Changer le statut | ADMIN, WAREHOUSE_MANAGER |

```json| DELETE | `/api/shipments/{id}` | Annuler une expédition | ADMIN |

{

  "totalOrders": 245,**Statuts disponibles :** `PENDING`, `IN_TRANSIT`, `DELIVERED`, `CANCELLED`

  "deliveredOrders": 230,

  "deliveryRate": 93.88,---

  "backorders": 15,

  "totalRevenue": 125000.50,### 📊 Rapports (`/api/reports`)

  "averageOrderValue": 510.20,

  "period": {| Méthode | Endpoint | Description | Rôles |

    "from": "2024-10-01",|---------|----------|-------------|-------|

    "to": "2024-11-04"| GET | `/api/reports/orders?fromDate=2024-10-01&toDate=2024-11-04` | Statistiques des commandes | ADMIN, WAREHOUSE_MANAGER |

  }| GET | `/api/reports/inventory?warehouseId=uuid` | Rapport d'inventaire | ADMIN, WAREHOUSE_MANAGER |

}| GET | `/api/reports/shipments?carrierId=uuid&fromDate=...&toDate=...` | Performance des expéditions | ADMIN, WAREHOUSE_MANAGER |

```| GET | `/api/reports/movements?warehouseId=uuid&fromDate=...&toDate=...` | Mouvements d'inventaire | ADMIN, WAREHOUSE_MANAGER |



---**Exemple de rapport de commandes :**

```json

## 🗄️ Modèle de données{

  "totalOrders": 245,

### Schéma des entités  "deliveredOrders": 230,

  "deliveryRate": 93.88,

```  "backorders": 15,

User (abstract)  "totalRevenue": 125000.50,

├── Manager (WAREHOUSE_MANAGER)  "averageOrderValue": 510.20,

│   └── warehouseId  "period": {

├── Client (CLIENT)    "from": "2024-10-01",

│   ├── name    "to": "2024-11-04"

│   └── contact  }

└── Admin (ADMIN)}

```

Product

├── id (UUID)---

├── sku (unique)

├── name## 🗄️ Modèle de données

├── description

├── price### Entités principales

├── active

└── inventory → List<Inventory>```

User (abstract)

Warehouse├── Manager (WAREHOUSE_MANAGER)

├── id (UUID)│   └── warehouseId

├── name├── Client (CLIENT)

├── location│   ├── name

├── active│   └── contact

└── inventory → List<Inventory>└── Admin (ADMIN)



InventoryProduct

├── id (UUID)├── id (UUID)

├── product → Product├── sku (unique)

├── warehouse → Warehouse├── name

├── qtyOnHand (quantité physique disponible)├── description

├── qtyReserved (quantité réservée pour commandes)├── price

├── reorderLevel├── active

└── maxStock└── inventory → List<Inventory>



SalesOrderWarehouse

├── id (UUID)├── id (UUID)

├── orderDate├── name

├── status (CREATED, RESERVED, SHIPPED, DELIVERED, CANCELLED)├── location

├── client → Client├── active

├── totalAmount└── inventory → List<Inventory>

└── orderLines → List<SalesOrderLine>

Inventory

PurchaseOrder├── id (UUID)

├── id (UUID)├── product → Product

├── orderDate├── warehouse → Warehouse

├── expectedDelivery├── quantity

├── status (CREATED, APPROVED, RECEIVED, CANCELLED)├── reorderLevel

├── supplier → Supplier└── maxStock

├── totalAmount

└── orderLines → List<PurchaseOrderLine>SalesOrder / PurchaseOrder

├── id (UUID)

InventoryMovement├── orderDate

├── id (UUID)├── status

├── movementDate├── totalAmount

├── movementType (INBOUND, OUTBOUND, ADJUSTMENT)└── orderLines → List<OrderLine>

├── quantity

├── referenceShipment

├── description├── id (UUID)

├── product → Product├── trackingNumber

└── warehouse → Warehouse├── status (PENDING, IN_TRANSIT, DELIVERED, CANCELLED)

├── shippedAt

Shipment├── deliveredAt

├── id (UUID)├── carrier → Carrier

├── trackingNumber├── warehouse → Warehouse

├── status (PENDING, IN_TRANSIT, DELIVERED, CANCELLED)└── order → SalesOrder

├── shippedAt

├── deliveredAtInventoryMovement

├── carrier → Carrier├── id (UUID)

├── warehouse → Warehouse├── movementDate

└── order → SalesOrder├── movementType (IN, OUT)

├── quantity

Supplier├── product → Product

├── id (UUID)└── warehouse → Warehouse

├── name```

├── contact

├── email**Stratégie d'héritage :** `JOINED` pour la hiérarchie `User`

└── address

**Relations :**

Carrier- `Manager` can manage many `Warehouses` — One-to-Many (Manager -> Warehouses).

├── id (UUID)- `Product` ↔ `Warehouse` : ManyToMany via `Inventory`

├── name- `Shipment` → `Carrier`, `Warehouse`, `SalesOrder` : ManyToOne

├── contact- `SalesOrder` → `Client` : ManyToOne

└── active

```---



**Stratégie d'héritage :** `JOINED` pour la hiérarchie `User`## 🔒 Sécurité



**Relations clés :**### Mécanisme JWT

- `Manager` → `Warehouse` : ManyToOne

- `Product` ↔ `Warehouse` : ManyToMany via `Inventory`1. **Authentification** : Login → JWT généré avec claims (email, rôles)

- `Inventory` → `Product`, `Warehouse` : ManyToOne2. **Autorisation** : JWT dans header `Authorization: Bearer <token>`

- `SalesOrder` → `Client` : ManyToOne3. **Validation** : Filter JWT vérifie signature & expiration

- `PurchaseOrder` → `Supplier` : ManyToOne4. **Révocation** : Logout ajoute le token à une blacklist

- `Shipment` → `Carrier`, `Warehouse`, `SalesOrder` : ManyToOne

- `InventoryMovement` → `Product`, `Warehouse` : ManyToOne### Endpoints publics (sans authentification)

- `/api/auth/register`

---- `/api/auth/login`

- `/swagger-ui/**`

## 🔒 Sécurité- `/v3/api-docs/**`



### Mécanisme JWT### Matrice de permissions



1. **Authentification** : Login → JWT généré avec claims (email, rôles)| Endpoint | ADMIN | WAREHOUSE_MANAGER | CLIENT |

2. **Autorisation** : JWT dans header `Authorization: Bearer <token>`|----------|-------|-------------------|--------|

3. **Validation** : Filter JWT vérifie signature & expiration| Managers CRUD | ✅ | ❌ | ❌ |

4. **Révocation** : Logout ajoute le token à une blacklist| Clients List/Create | ✅ | ✅ | ❌ |

| Products CRUD | ✅ | ✅ (lecture seule) | ❌ |

### Endpoints publics (sans authentification)| Warehouses CRUD | ✅ | ✅ (lecture seule) | ❌ |

- `/api/auth/register`| Shipments | ✅ | ✅ | ❌ |

- `/api/auth/login`| Reports | ✅ | ✅ | ❌ |

- `/swagger-ui/**`

- `/v3/api-docs/**`---



### Matrice de permissions## 🧪 Tests



| Endpoint | ADMIN | WAREHOUSE_MANAGER | CLIENT |### Exécuter les tests

|----------|-------|-------------------|--------|

| Managers CRUD | ✅ | ❌ | ❌ |```bash

| Clients CRUD | ✅ | ✅ | ❌ |# Tous les tests

| Products CRUD | ✅ | ✅ | ❌ |./mvnw test

| Warehouses CRUD | ✅ | ✅ (lecture) | ❌ |

| Inventory | ✅ | ✅ | ❌ |# Tests d'une classe spécifique

| Inventory Movements | ✅ | ✅ | ❌ |./mvnw test -Dtest=ManagerControllerTest

| Sales Orders Create | ✅ | ✅ | ✅ |

| Sales Orders Manage | ✅ | ✅ | ❌ |# Tests avec couverture (JaCoCo)

| Purchase Orders | ✅ | ✅ | ❌ |./mvnw clean test jacoco:report

| Suppliers | ✅ | ✅ | ❌ |# Rapport disponible dans target/site/jacoco/index.html

| Carriers | ✅ | ✅ | ❌ |

| Shipments | ✅ | ✅ | ❌ |# Skip tests lors du build

| Reports | ✅ | ✅ | ❌ |./mvnw clean package -DskipTests

```

---

---

## 🧪 Tests

## 🚀 Déploiement

### Exécuter les tests

### Production avec Docker

```bash

# Tous les tests```bash

./mvnw test# 1. Build l'image

docker build -t digital-logistics:latest .

# Tests d'une classe spécifique

./mvnw test -Dtest=ManagerControllerTest# 2. Tag pour registry

docker tag digital-logistics:latest registry.example.com/digital-logistics:1.0.0

# Tests avec couverture (JaCoCo)

./mvnw clean test jacoco:report# 3. Push vers registry

# Rapport disponible dans target/site/jacoco/index.htmldocker push registry.example.com/digital-logistics:1.0.0



# Skip tests lors du build# 4. Déployer sur serveur

./mvnw clean package -DskipTestsdocker run -d \

```  --name digital-logistics-prod \

  --restart unless-stopped \

---  -p 8090:8090 \

  -e SPRING_PROFILES_ACTIVE=prod \

## 🚀 Déploiement  -e SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db-host:5432/logistics_prod \

  -e SPRING_DATASOURCE_USERNAME=prod_user \

### Production avec Docker  -e SPRING_DATASOURCE_PASSWORD=super_secure_password \

  -e JWT_SECRET=production_jwt_secret_min_32_chars \

```bash  -e SPRING_JPA_HIBERNATE_DDL_AUTO=validate \

# 1. Build l'image  registry.example.com/digital-logistics:1.0.0

docker build -t digital-logistics:latest .```



# 2. Tag pour registry---

docker tag digital-logistics:latest registry.example.com/digital-logistics:1.0.0

## 🔧 Troubleshooting

# 3. Push vers registry

docker push registry.example.com/digital-logistics:1.0.0### Problème : Port déjà utilisé



# 4. Déployer sur serveur```bash

docker run -d \# Trouver le processus utilisant le port 8090

  --name digital-logistics-prod \lsof -i :8090

  --restart unless-stopped \# ou

  -p 8090:8090 \netstat -ano | findstr :8090

  -e SPRING_PROFILES_ACTIVE=prod \

  -e SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db-host:5432/logistics_prod \# Tuer le processus

  -e SPRING_DATASOURCE_USERNAME=prod_user \kill -9 <PID>

  -e SPRING_DATASOURCE_PASSWORD=super_secure_password \```

  -e JWT_SECRET=production_jwt_secret_min_32_chars \

  -e SPRING_JPA_HIBERNATE_DDL_AUTO=validate \### Problème : Erreur de connexion PostgreSQL

  registry.example.com/digital-logistics:1.0.0

```**Symptôme :** `Connection refused` ou `Authentication failed`



---**Solutions :**

1. Vérifier que PostgreSQL est démarré : `docker ps` ou `systemctl status postgresql`

## 🔧 Troubleshooting2. Vérifier les credentials dans `application.yml` ou variables d'environnement

3. Vérifier la base de données existe : `psql -U postgres -c "\l"`

### Problème : Port déjà utilisé4. Tester la connexion : `psql -h localhost -U postgres -d digital_logistics`



```bash### Problème : Swagger UI ne charge pas

# Trouver le processus utilisant le port 8090

lsof -i :8090**Solutions :**

# ou sur Windows1. Vérifier l'URL : http://localhost:8090/swagger-ui/index.html (pas swagger-ui.html)

netstat -ano | findstr :80902. Vérifier la configuration dans `application.yml`

3. Vérifier les logs pour erreurs SpringDoc

# Tuer le processus4. Tester `/v3/api-docs` : http://localhost:8090/v3/api-docs

kill -9 <PID>

# ou sur Windows### Problème : Docker build échoue

taskkill /PID <PID> /F

```**Symptôme :** `mvnw: Permission denied`



### Problème : Erreur de connexion PostgreSQL**Solution :**

```bash

**Symptôme :** `Connection refused` ou `Authentication failed`# Dans le Dockerfile, ajouter :

RUN chmod +x /app/mvnw

**Solutions :**

1. Vérifier que PostgreSQL est démarré : `docker ps` ou `systemctl status postgresql`# Ou localement :

2. Vérifier les credentials dans `application.yml` ou variables d'environnementchmod +x mvnw

3. Vérifier la base de données existe : `psql -U postgres -c "\l"`git update-index --chmod=+x mvnw

4. Tester la connexion : `psql -h localhost -U postgres -d digital_logistics````



### Problème : Swagger UI ne charge pas### Logs et debugging



**Solutions :**```bash

1. Vérifier l'URL : http://localhost:8090/swagger-ui/index.html# Logs Docker Compose

2. Vérifier la configuration dans `application.yml`docker-compose logs -f app

3. Vérifier les logs pour erreurs SpringDoc

4. Tester `/v3/api-docs` : http://localhost:8090/v3/api-docs# Logs container spécifique

docker logs -f <container-id>

### Problème : Docker build échoue

# Entrer dans le container

**Symptôme :** `mvnw: Permission denied`docker exec -it digital-logistics-app sh



**Solution :**# Vérifier la connectivité DB depuis le container

```bashdocker exec -it digital-logistics-app sh -c "nc -zv postgres 5432"

# Dans le Dockerfile, ajouter :```

RUN chmod +x /app/mvnw

---

# Ou localement :

chmod +x mvnw## 📝 Notes de version

git update-index --chmod=+x mvnw

```### Version 1.0.0 (2024-11-04)



### Problème : Stock réservé n'est pas libéré**Fonctionnalités initiales :**

- ✅ Authentification JWT complète

**Solution :**- ✅ CRUD Managers (ADMIN uniquement)

Vérifier que les commandes annulées/livrées libèrent le stock réservé en re-créditant `qtyReserved` vers `qtyOnHand`.- ✅ CRUD Clients

- ✅ CRUD Produits avec pagination

### Logs et debugging- ✅ CRUD Entrepôts

- ✅ Gestion des expéditions

```bash- ✅ 4 rapports statistiques (commandes, inventaire, expéditions, mouvements)

# Logs Docker Compose- ✅ Documentation Swagger UI

docker-compose logs -f app- ✅ Containerisation Docker

- ✅ Docker Compose avec PostgreSQL et pgAdmin

# Logs container spécifique

docker logs -f <container-id>**Améliorations futures :**

- [ ] Migration Flyway/Liquibase

# Entrer dans le container- [ ] Cache Redis pour performances

docker exec -it digital-logistics-app sh- [ ] Monitoring avec Actuator + Prometheus

- [ ] Notifications par email/SMS

# Vérifier la connectivité DB depuis le container- [ ] Export de rapports PDF/Excel

docker exec -it digital-logistics-app sh -c "nc -zv postgres 5432"- [ ] API GraphQL en complément REST

```- [ ] Tests de charge (JMeter/Gatling)

- [ ] CI/CD avec GitHub Actions

---

---

## 📝 Notes de version

**Made by Mohammed ENNAIM**

### Version 1.0.0 (2024-11)

**Fonctionnalités principales :**
- ✅ Authentification JWT complète avec rôles (ADMIN, WAREHOUSE_MANAGER, CLIENT)
- ✅ CRUD Managers (ADMIN uniquement)
- ✅ CRUD Clients
- ✅ CRUD Produits avec pagination et recherche
- ✅ CRUD Entrepôts
- ✅ Gestion de l'inventaire (qtyOnHand, qtyReserved)
- ✅ **Réservation automatique d'inventaire** lors de la création de commandes de vente
- ✅ **Commandes fournisseurs** (Purchase Orders) avec approbation et réception
- ✅ **Mouvements de stock** (INBOUND/OUTBOUND/ADJUSTMENT)
- ✅ Gestion des fournisseurs et transporteurs
- ✅ Gestion des expéditions
- ✅ 4 rapports statistiques (commandes, inventaire, expéditions, mouvements)
- ✅ Documentation Swagger UI
- ✅ Containerisation Docker
- ✅ Docker Compose avec PostgreSQL et pgAdmin

**Améliorations futures :**
- [ ] Migration Flyway/Liquibase pour schéma DB
- [ ] Libération automatique du stock réservé (annulation/livraison)
- [ ] Cache Redis pour performances
- [ ] Monitoring avec Actuator + Prometheus
- [ ] Notifications par email/SMS
- [ ] Export de rapports PDF/Excel
- [ ] API GraphQL en complément REST
- [ ] Tests de charge (JMeter/Gatling)
- [ ] CI/CD avec GitHub Actions

---

## 📧 Contact

**Auteur :** Mohammed ENNAIM

**Repository :** [https://github.com/mohammedennaim/SpringBootProject](https://github.com/mohammedennaim/SpringBootProject)

---

**License :** MIT
