###############################
# Build stage
###############################
FROM amazoncorretto:17-alpine AS build

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
FROM amazoncorretto:17-alpine

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy the fat jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R spring:spring /app

# Set a default server port and JAVA_HOME
ENV SERVER_PORT=8093
ENV JAVA_HOME=/usr/lib/jvm/default-jvm
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE ${SERVER_PORT}

# Run the application
CMD ["java","-jar","app.jar"]
