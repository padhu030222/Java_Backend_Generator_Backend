# ===== Stage 1: Build the application =====
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# ===== Stage 2: Run the built jar =====
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9000
ENV PORT=9000
ENTRYPOINT ["java", "-jar", "app.jar"]
