# Étape 1 : Construction de l'application
FROM maven:3.9.0-eclipse-temurin-17 AS builder

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers nécessaires pour le build
COPY pom.xml .
COPY src ./src

# Construire le projet Maven
RUN mvn clean package -DskipTests

# Étape 2 : Création de l'image exécutable
FROM eclipse-temurin:17-jdk-alpine

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR de l'étape de construction
COPY --from=builder target/cours-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port par défaut de Spring Boot
EXPOSE 8080

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
