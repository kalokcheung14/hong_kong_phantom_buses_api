# Use a base image with OpenJDK 11
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file into the container
COPY target/phantom-buses-0.1.0.jar /app/phantom-buses-0.1.0.jar

# Expose the port your SpringBoot application runs on
EXPOSE 8082

# Command to run the SpringBoot application
ENTRYPOINT ["java", "-jar", "/app/phantom-buses-0.1.0.jar"]
