FROM openjdk:17-jdk-alpine
EXPOSE 8089
ADD kaddem-0.0.2.jar kaddem-0.0.2.jar
ENTRYPOINT ["java", "-jar", "/kaddem-0.0.2.jar"]