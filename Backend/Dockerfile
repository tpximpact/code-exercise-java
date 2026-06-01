# Build stage
FROM gradle:8.14.3-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Cache dependencies
RUN gradle dependencies --no-daemon

COPY src ./src

# Build application
RUN gradle bootJar --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080 5005

ENTRYPOINT ["java", "-jar", "app.jar"]
