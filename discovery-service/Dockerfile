# ----
# Dockerfile for product-services
# ----

# -----
# Build stage
# -----
# Base image
FROM gradle:8-jdk17 AS builder

# Copy the application files from the host machine for the image filesystem
COPY --chown=gradle:gradle . /home/gradle/src

# Set the directory within the image, for executing future commands
WORKDIR /home/gradle/src

# Run gradle build withihn the container - NOT the host machine
RUN gradle build -x test


# -----
# Package Stage
# -----
# Base image
FROM openjdk:17

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/discovery-service.jar


EXPOSE 8761

ENTRYPOINT ["java","-jar","/app/discovery-service.jar"]











