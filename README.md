# 🛒 MicroMart: Enterprise E-Commerce Ecosystem

![CI Pipeline](https://github.com/Mayorman07/Micromart/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)

MicroMart is a high-availability, event-driven e-commerce backend built on a **Microservices Architecture**. It features 10 independent services orchestrated via Spring Cloud, synchronized through a shared security library, and integrated via RabbitMQ.

---

## 🏗️ System Architecture

This ecosystem follows the **API Gateway Pattern** and **Service Discovery Pattern** to ensure high scalability and loose coupling.

```mermaid
graph TD
Client((Customer App)) --> Gateway[API Gateway: 7070]

subgraph "Infrastructure"
    Config[Config Server: 8888]
    Eureka[Eureka Discovery: 8761]
    Rabbit[RabbitMQ Broker]
end

subgraph "Business Logic Services"
    Users[Users Service]
    Products[Products Service]
    Cart[Cart Service]
    Order[Order Service]
    Inventory[Inventory Service]
    Payment[Payment Service]
    Notification[Notification Service]
end

subgraph "Shared Core"
    JWT[[JwtAuthorities Library]]
end

%% Relations
Gateway -.-> Eureka
Users & Products & Order & Payment -.-> JWT
Order -- "OrderPlaced Event" --> Rabbit
Rabbit -- "Consume" --> Notification

```