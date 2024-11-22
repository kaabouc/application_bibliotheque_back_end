# Étape 1 : Utiliser une image Maven officielle pour construire l'application
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers du projet
COPY pom.xml .
COPY src ./src

# Compiler le projet et construire un fichier jar exécutable
RUN mvn clean package -DskipTests

# Étape 2 : Utiliser une image JDK officielle pour exécuter l'application
FROM eclipse-temurin:17-jre-alpine

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR de l'étape de construction
COPY --from=builder /app/target/*.jar app.jar

# Exposer le port utilisé par l'application Spring Boot
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
