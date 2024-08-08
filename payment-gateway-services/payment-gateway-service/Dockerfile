FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/payment-gateway-service-0.0.1-SNAPSHOT.jar payment-gateway-service.jar
ENTRYPOINT ["java", "-jar", "/payment-gateway-service.jar"]