FROM openjdk:8-jre-alpine
COPY build/libs/socket-it-1.5.0.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]