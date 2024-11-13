# Utilisation de l'image OpenJDK 17 comme base
FROM openjdk:17-jdk-alpine

# Ajouter un argument pour le fichier JAR
ARG JAR_FILE=target/*.jar

# Copier le fichier JAR dans le conteneur
COPY ${JAR_FILE} app.jar

EXPOSE 8089

# Lancer l'application Java Spring Boot
ENTRYPOINT ["java","-jar","/app.jar"]

