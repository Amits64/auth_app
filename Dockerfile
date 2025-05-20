# Build stage
FROM gradle:8.4.0-jdk17-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/app/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]