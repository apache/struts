# Base image
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /struts-app

# Copy project files
COPY . /struts-app

# Run Maven package command to build the project
RUN mvn clean package

# Expose port (adjust as needed)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/struts-app.jar"]
