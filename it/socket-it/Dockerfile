FROM openjdk:8-jre-alpine
COPY build/libs/socket-it-2.3.1.jar /app/app.jar
WORKDIR /app
CMD ["java", "-jar", "app.jar"]