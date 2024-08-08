FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/bank-reconciliation-service-0.0.1-SNAPSHOT.jar bank-reconciliation-service.jar
ENTRYPOINT ["java", "-jar", "/bank-reconciliation-service.jar"]