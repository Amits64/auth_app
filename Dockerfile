# Build stage
FROM gradle:8.4.0-jdk17-alpine AS build
WORKDIR /app
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle
COPY src src
RUN ./gradlew build -x test

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
EXPOSE 8080

# Set environment variables (customize as needed)
ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_DATASOURCE_URL=jdbc:mysql://192.168.10.10:3306/auth_db?useSSL=false

ENTRYPOINT ["java", "-jar", "/app/app.jar"]