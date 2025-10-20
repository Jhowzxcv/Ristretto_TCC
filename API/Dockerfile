# Build da aplicação
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /API
COPY . .
RUN ./mvnw clean package -DskipTests

# Runtime
FROM eclipse-temurin:17-jdk
WORKDIR /API
COPY --from=build /API/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
