FROM openjdk:17.0.1-jdk-slim

WORKDIR /app

COPY target/urlgateway-0.0.1-SNAPSHOT.jar .

EXPOSE 80

ENV SERVER_PORT=80

ENTRYPOINT ["java", "-jar", "urlgateway-0.0.1-SNAPSHOT.jar"]