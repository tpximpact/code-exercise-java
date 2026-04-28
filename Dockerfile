# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy only the built jar from the build stage
COPY --from=build /app/target/url_shortener-1.0.0.jar app.jar

# expose port
EXPOSE 8080

# run app
ENTRYPOINT ["java", "-jar", "app.jar"]