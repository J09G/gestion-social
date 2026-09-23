# Etapa 1: compilar el backend
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia el pom y descarga dependencias (aprovecha cache)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia el código y compila
COPY src ./src
RUN mvn -B clean package -DskipTests

# Etapa 2: imagen final solo con Java
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia el jar generado
COPY --from=build /app/target/gestion-social-0.0.1-SNAPSHOT.jar app.jar

# Render/Railway inyectan PORT; Spring usa ${PORT:8080}
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]