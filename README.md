# Digital Logistics — README

Bref: API Spring Boot pour la gestion logistique (PostgreSQL). Ce dépôt contient les entités JPA, repositories Spring Data, services et controllers minimalistes. Le projet est prévu pour être exécuté en local via Maven ou via Docker Compose (Postgres + pgAdmin + app).

## Contenu principal

- `pom.xml` — configuration Maven (Spring Boot 3.5.x, Java 17, MapStruct, Lombok)
- `Dockerfile` — Dockerfile de développement (lance `mvn spring-boot:run` pour live-reload)
- `docker-compose.yaml` — services: `postgres`, `pgadmin`, `app` (monorepo image)
- `src/main/java/com/example/digitallogistics` — code source Java
  - `LogisticsApiApplication.java` — point d'entrée Spring Boot
  - `controller/` — controllers REST (ex: `UserController`, `TestController`)
  - `model/` — `entity/`, `dto/`, `enums/`, `mapper/`
  - `repository/` — Spring Data JPA repositories
  - `service/` — services métiers
  - `config/`, `exception/`, `jobs/`, `util/`
- `src/main/resources/application.yml` — configuration Spring Boot
- `src/main/resources/data.sql` — script d'initialisation (DDL + INSERTs) monté dans Postgres container

## Structure de fichiers (extraits)

```
docker-compose.yaml
Dockerfile
pom.xml
src/
  main/
    java/com/example/digitallogistics/
      controller/
        UserController.java
        TestController.java
      model/
        entity/
        enums/
        dto/
      repository/
      service/
    resources/
      application.yml
      data.sql
```

## Endpoints importants (User)
Base path: `/api/users`

- GET `/api/users` — liste tous les users (200 OK)
- GET `/api/users/{id}` — récupère user par UUID (200 / 404)
- GET `/api/users/by-email?email=...` — récupère par email (200 / 404)
- GET `/api/users/role/{role}` — récupère users par rôle (200, peut être vide)
- POST `/api/users` — crée un user (201 Created + Location header)
- PUT `/api/users/{id}` — met à jour un user (200 / 404)
- DELETE `/api/users/{id}` — supprime (204 No Content)

Exemples (adapter le port et UUID):

```bash
# lister
curl -i http://localhost:8080/api/users

# récupérer par id
curl -i http://localhost:8080/api/users/123e4567-e89b-12d3-a456-426614174000

# récupérer par email
curl -i "http://localhost:8080/api/users/by-email?email=someone@example.com"

# récupérer par role
curl -i http://localhost:8080/api/users/role/ADMIN

# créer
curl -i -H 'Content-Type: application/json' -d '{"id":"<uuid>","email":"a@b.com","password":"pass","role":"ADMIN","active":true}' http://localhost:8080/api/users

```

> Remarque: le mapping pour le rôle est `/role/{role}` (évite l'ambiguïté avec `/{id}`).

## Build & exécution

1) En local (Maven)

```bash
# compiler et packager
./mvnw -DskipTests package

# exécuter localement (jar)
java -jar target/digital-logistics-1.0.0.jar
```

2) Avec Docker Compose (développement)

- Copier `.env.example` en `.env` et ajuster les variables (`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `SERVER_PORT`, ...)
- Démarrer les services:

```bash
docker compose up --build -d
```

- Par défaut le service `app` lit `SERVER_PORT` (défini dans `.env`). Vérifiez le port exposé dans la sortie de `docker compose ps`.

Notes importantes:
- Le `Dockerfile` fourni est orienté développement: il exécute `mvn spring-boot:run` et inclut `spring-boot-devtools`. Cela provoque parfois des redémarrages (devtools) et des réponses vides lors des rebuilds. Pour un container stable (production-like), préférez changer le Dockerfile pour exécuter le jar repackagé : `java -jar target/*.jar`.

Exemple de Dockerfile (extrait recommandé pour build d'image finale):

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/digital-logistics-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

3) Re-créer la DB seed

- Le script `src/main/resources/data.sql` est monté dans `/docker-entrypoint-initdb.d/` du container Postgres. Il ne s'exécute que lors de l'initialisation d'un volume neuf. Pour ré-appliquer le seed, supprimez le volume `pg_data` puis relancez `docker compose up`:

```bash
docker compose down
docker volume rm <project>_pg_data  # ou `docker volume ls` pour trouver le nom
docker compose up --build
```

## Debugging courant

- `No static resource api/users.`: cela signifie qu'aucun controller ne correspond au chemin demandé — souvent dû à une route mal mappée ou au controller non chargé. Vérifiez les logs (`docker compose logs app --tail 200`) ; cherchez `Tomcat started` et `Started LogisticsApiApplication`.
- `Empty reply from server` pendant les rebuilds: fréquent si devtools/maven-run à l'intérieur du container redémarre le processus. Solution: lancer le jar directement pour stabilité.

## Tests

- Les dépendances de test sont configurées (`spring-boot-starter-test`). Les tests unitaires se trouvent sous `src/test`. Pour lancer les tests:

```bash
./mvnw test
```

## Prochaines améliorations suggérées

- Convertir `data.sql` en migrations Flyway/Liquibase pour un contrôle plus sûr des migrations.
- Remplacer l'exécution `mvn spring-boot:run` dans Docker par le jar packagé pour stabilité en conteneur.
- Ajouter DTOs + MapStruct mappers pour séparer entités et API.
- Ajouter authentification / hashing des mots de passe avant stockage.

## Contacts / références

- Point d'entrée: `src/main/java/com/example/digitallogistics/LogisticsApiApplication.java`
- Fichier d'initialisation DB: `src/main/resources/data.sql`

---

Si vous voulez, je peux:
- générer automatiquement un `README.md` plus détaillé avec tous les endpoints (tous les controllers), ou
- modifier le Dockerfile pour produire une image basée sur le jar (stable) et adapter `docker-compose.yaml` (je peux appliquer ce changement et redéployer).

Dites-moi quelle option vous préférez (générer plus de docs OU adapter Docker pour exécuter le jar). 

test automatisation