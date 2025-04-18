# Build stage
FROM eclipse-temurin:21-jdk-alpine as build

# Install Maven and other required packages
RUN apk add --no-cache maven bash

# Set working directory
WORKDIR /workspace/app

# Copy project files
COPY pom.xml ./
COPY src ./src/

# Build the application using Maven directly
RUN mvn install -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"] 