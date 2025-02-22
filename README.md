# Digital Wallet Microservice Application

## Overview

The Digital Wallet Microservice Application is a scalable and secure platform for managing digital wallets and transactions. Built with Java and Spring Boot, it leverages a microservices architecture and incorporates Test-Driven Development (TDD) practices.

## Features

- Scalable microservices architecture
- Real-time data streaming using Kafka
- Containerized with Docker
- Monitoring with Prometheus and Grafana
- Logging with SLF4J
- CI/CD with GitHub Actions
- API documentation with Swagger

## Technology Stack

- **Backend:** Java, Spring Boot, Spring Security, Spring Data JPA
- **Messaging:** Kafka
- **Containerization:** Docker
- **Database:** PostgreSQL
- **Logging:** SLF4J
- **CI/CD:** GitHub Actions
- **Testing:** JUnit, Mockito, Testcontainers
- **API Documentation:** Swagger (OpenAPI)

## Getting Started

1. **Clone the Repository**

    ```bash
    git clone https://github.com/stratpoint-training/java-electric-titans.git
    ```

2. **Build and Run**

    ```bash
    ./mvnw clean package
    docker-compose up -d
    ```

3. **Access Services**

    - **Kafka UI**: [http://localhost:8080](http://localhost:8080)


4. **Access Swagger UI**

    Once the application is running, you can access the Swagger UI to explore the API endpoints at:

    - **User Service**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui/index.html)
    - **Transaction Service**: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui/index.html)
    - **Payment Gateway Service**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui/index.html)
    - **Bank Reconciliation Service**: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui/index.html)
    - **Account Service**: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui/index.html)
    - **Notification Service**: [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui/index.html)


## Testing

- **Unit Tests:** `./mvnw test`
- **Integration Tests:** `./mvnw verify`


## Contributors

- **JC Ilacad** - User and Account Servce
- **Sam Nieva** - Transaction Service
- **Jay Abrenica** - Payment Gateway Service
- **Bea Franco** - Bank Reconciliation


---

Thank you for exploring the Digital Wallet Microservice Application!
