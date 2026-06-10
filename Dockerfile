# Fase 1: Build (compilar y generar el .jar)
# Usa una imagen que trae Maven + Java 21. tiene todo lo necesario para compilar.
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Crea la carpeta /app dentro del contenedor 
# y copia el pom.xml (las dependencias) y el código fuente.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Compila el proyecto: descarga dependencias, 
# compila las clases Java y genera un .jar ejecutable
RUN mvn clean package -DskipTests



# Fase 2: Runtime (ejecutar la app)
# Imagen nueva, solo tiene el JRE
FROM eclipse-temurin:21-jre

WORKDIR /app
# Copia el .jar de la fase anterior a esta imagen. 
# El --from=build referencia la primera fase.
COPY --from=build /app/target/*.jar app.jar
EXPOSE 80
# Forzamos el perfil de producción al arrancar
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=prod"]