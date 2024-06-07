# Use the official OpenJDK 21 base image
FROM openjdk:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled JAR file into the container
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Define the command to run your application
CMD ["java", "-jar", "app.jar"]