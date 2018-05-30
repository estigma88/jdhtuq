FROM openjdk:8-jre-alpine
COPY build/libs/integration-it-2.0.0.jar /app/app.jar
WORKDIR /app
CMD ["java", "-jar", "app.jar"]