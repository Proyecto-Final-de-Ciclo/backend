# ==========================
# Fase 1: Build (compilar y generar el .jar)
# ==========================
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================
# Fase 2: Runtime (ejecutar la app)
# ==========================
FROM eclipse-temurin:21-jre
WORKDIR /app
# *.jar para no depender del nombre exacto (tu artifactId es "demo")
COPY --from=build /app/target/*.jar app.jar
EXPOSE 80
# Forzamos el perfil de producción al arrancar
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=prod"]