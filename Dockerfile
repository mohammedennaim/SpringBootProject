###############################
# Build stage
###############################
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and pom first to leverage layer caching
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Make mvnw executable
RUN chmod +x mvnw

# Pre-download dependencies
RUN ./mvnw dependency:go-offline -B || true

# Copy application sources
COPY src ./src

# Build the Spring Boot jar (tests skipped for faster Docker builds)
RUN ./mvnw clean package -DskipTests

# List the target directory to verify the JAR was created
RUN ls -la /app/target/

###############################
# Runtime stage
###############################
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the fat jar from the build stage with explicit name
COPY --from=build /app/target/digital-logistics-1.0.0.jar app.jar

# Align with application's default port (overridable via SERVER_PORT env)
EXPOSE 8093

# Run the packaged Spring Boot application
CMD ["java","-jar","app.jar"]
