# Stage 1: Build the application with Maven

FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /secure-team-3-todo-api
 
# Copy the entire project source

COPY . .
 
# Build the application, skipping tests

RUN mvn clean package -DskipTests
 
# Stage 2: Create the final lightweight runtime image

FROM eclipse-temurin:21-jre-jammy

WORKDIR /secure-team-3-todo-api
 
# Correctly copy the JAR from the 'build' stage

COPY --from=build /secure-team-3-todo-api/target/*.jar app.jar
 
EXPOSE 80
 
# Set the entrypoint to run the application

ENTRYPOINT ["java", "-jar", "app.jar"]