# Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia só o pom.xml primeiro e baixa dependências (camada cacheada)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora copia o código e builda
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]