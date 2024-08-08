FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/transaction-service-0.0.1-SNAPSHOT.jar transaction-service.jar
ENTRYPOINT ["java", "-jar", "/transaction-service.jar"]