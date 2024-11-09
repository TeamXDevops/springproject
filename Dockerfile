FROM openjdk:11-jre-slim
WORKDIR /app
COPY target/your-app.jar /app/your-app.jar
ENTRYPOINT ["java", "-jar", "/app/your-app.jar"]

