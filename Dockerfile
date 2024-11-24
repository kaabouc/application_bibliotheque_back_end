# Étape 1 : Construction de l'application
FROM maven:3.9.0-eclipse-temurin-17 AS builder

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers nécessaires pour le build
COPY pom.xml .
COPY src ./src

# Afficher le contenu du répertoire pour le débogage
RUN ls -la

# Construire le projet Maven avec plus de logs
RUN mvn clean package -DskipTests -X

# Afficher le contenu du répertoire target pour le débogage
RUN ls -la target/

# Étape 2 : Création de l'image exécutable
FROM eclipse-temurin:17-jdk-alpine

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR de l'étape de construction
COPY --from=builder /app/target/*.jar app.jar

# Exposer le port par défaut de Spring Boot
EXPOSE 9000

# Commande pour exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]