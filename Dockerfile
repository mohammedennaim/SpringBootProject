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

# Copy the fat jar from the build stage using a wildcard so versioned jar names won't break the build
# It will copy the first matching jar into app.jar inside the runtime image
COPY --from=build /app/target/*.jar app.jar

# Set a default server port and JAVA_HOME for clarity
ENV SERVER_PORT=8093
ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Align with application's default port (overridable via SERVER_PORT env)
EXPOSE ${SERVER_PORT}

# Run the packaged Spring Boot application
CMD ["java","-jar","app.jar"]
