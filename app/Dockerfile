# ==========================================
# MULTI-STAGE DOCKERFILE FOR SPRING BOOT APP
# ==========================================

# Stage 1: Build and compilation stage
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /build

# Copy pom.xml and source code into the build context
COPY pom.xml .
COPY src ./src

# Package the application skipping execution of unit tests for speed optimization
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime image stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the compiled executable JAR artifact from the builder stage target directory
COPY --from=builder /build/target/*.jar app.jar

# Expose web application embedded server port
EXPOSE 8080

# Define application execution environment entrypoint parameters
ENTRYPOINT ["java", "-jar", "app.jar"]
