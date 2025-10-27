# =====================================================
# Développement Dockerfile — live reload support
# =====================================================

FROM eclipse-temurin:17-jdk-alpine

# Définir dossier de travail
WORKDIR /app

# Copier uniquement fichiers Maven + code source
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Installer dépendances (cache Maven)
RUN ./mvnw dependency:go-offline -B

# ⛔ لا نبني jar هنا، سنشغّل mvn directement أثناء runtime
COPY src ./src

# Exposer port
EXPOSE 8080

# Commande pour lancer Spring Boot en mode dev
CMD ["./mvnw", "spring-boot:run"]