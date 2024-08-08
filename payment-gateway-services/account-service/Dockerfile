FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/account-service-0.0.1-SNAPSHOT.jar account-service.jar
ENTRYPOINT ["java", "-jar", "/account-service.jar"]