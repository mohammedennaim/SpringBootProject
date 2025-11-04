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
RUN ./mvnw dependency:go-offline -B

# Copy application sources
COPY src ./src

# Build the Spring Boot jar (tests skipped for faster Docker builds)
RUN ./mvnw package -DskipTests

###############################
# Runtime stage
###############################
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the fat jar from the build stage
COPY --from=build /app/target/digital-logistics-*.jar app.jar

# Align with application's default port (overridable via SERVER_PORT env)
EXPOSE 8090

# Run the packaged Spring Boot application
ENTRYPOINT ["java","-jar","app.jar"]
