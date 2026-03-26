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
Client((Customer App)) --> Gateway[API Gateway: 7082]

subgraph "Infrastructure"
    Config[Config Server: 7012]
    Eureka[Eureka Discovery: 7010]
    Rabbit[RabbitMQ Messaging Broker]
end

subgraph "Business Logic Services"
    Users[Users Service : 0]
    Products[Products Service : 7016]
    Cart[Cart Service : 7041]
    Order[Order Service : 7063]
    Inventory[Inventory Service : 7061]
    Payment[Payment Service: 7007]
    Notification[Notification Service : 7050]
end

subgraph "Shared Core"
    JWT[[JwtAuthorities Library]]
end

%% Relations
Gateway -.-> Eureka
Users & Products & Order & Payment & Inventory & Cart -.-> JWT
Payment -- Order -- "OrderPlaced Event" --> Rabbit
Order -- "OrderPlaced Event" --> Rabbit
Rabbit -- "Consume" --> Notification --> Customer Email [Order status]
Rabbit -- "Consume" --> Notification --> Cart & Inventory [Update status]
Payment -- "Payment Status Event" --> Rabbit
Rabbit -- "Consume" --> Notification --> Customer Email [Payment status]

``````mermaid
graph TD
    Client((Customer App)) --> Gateway[API Gateway: 7082]
    
    subgraph "Infrastructure"
        Config[Config Server: 7012]
        Eureka[Eureka Discovery: 7010]
        Rabbit[RabbitMQ Broker]
    end

    subgraph "Business Logic Services"
        Users[Users Service: 0]
        Products[Products Service: 7016]
        Cart[Cart Service: 7041]
        Order[Order Service: 7063]
        Inventory[Inventory Service: 7061]
        Payment[Payment Service: 7007]
        Notification[Notification Service: 7050]
    end

    subgraph "Shared Core"
        JWT[[JwtAuthorities Library]]
    end

    %% Infrastructure & Security
    Gateway -.-> Eureka
    Users & Products & Order & Payment & Inventory & Cart -.-> JWT

    %% Synchronous REST Flow (The Setup)
    Gateway --> Users & Products & Cart & Order
    Cart -- "Sync: Check Stock" --> Inventory
    Order -- "Sync: Request Link" --> Payment

    %% Asynchronous RabbitMQ Flow (The Ripple Effect)
    Order -- "Pub: OrderPlaced" --> Rabbit
    Payment -- "Pub: PaymentSuccess" --> Rabbit
    
    Rabbit -. "Sub: Update Status" .-> Order
    Rabbit -. "Sub: Deduct Stock" .-> Inventory
    Rabbit -. "Sub: Clear Basket" .-> Cart
    Rabbit -. "Sub: Send Email" .-> Notification
    
```