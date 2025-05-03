# Use a lightweight JDK base image
FROM eclipse-temurin:21-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Gradle/Maven files first for dependency caching (use one of these depending on your build tool)
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Download dependencies first
RUN ./mvnw dependency:go-offline -B

# Copy full source and build
COPY . .
RUN ./mvnw package -DskipTests

# ---- Runtime Image ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/country-service-*.jar app.jar

# Expose port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
