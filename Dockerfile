# Use a base image with Java installed
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file into the container
COPY target/springproject.jar /app/springproject.jar

# Expose the port that the application will run on
EXPOSE 8080

# Define the command to run the application
CMD ["java", "-jar", "springproject.jar"]
