# Multi-stage Dockerfile for a Gradle-based Spring Boot (Groovy) application
# Stage 1: build with Gradle
FROM gradle:7.6-jdk17 AS builder
WORKDIR /home/gradle/project
# Copy project files
COPY --chown=gradle:gradle . .
# Ensure the Gradle wrapper is executable and build the bootJar (skip tests to keep build fast)
RUN chmod +x ./gradlew || true \
    && ./gradlew bootJar -x test --no-daemon

# Stage 2: smaller runtime image
FROM eclipse-temurin:17-jre-jammy
# Create non-root user
RUN useradd -m appuser || true
WORKDIR /home/app
# Copy the built jar from the builder stage
COPY --from=builder /home/gradle/project/build/libs/*.jar /home/app/app.jar
# Expose default Spring Boot port (can be overridden)
EXPOSE 8080
# Allow users to pass JVM options via JAVA_OPTS
ENV JAVA_OPTS="-Xms256m -Xmx512m"
# Run as non-root for better security
USER appuser
# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /home/app/app.jar"]

# Optional: build with --platform=linux/amd64 if building on non-Linux hosts and needed.
# To build: docker build -t vosapi:latest .
# To run:  docker run -p 8080:8080 --env "SPRING_PROFILES_ACTIVE=prod" vosapi:latest

