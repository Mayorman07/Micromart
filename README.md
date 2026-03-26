# 🛒 MicroMart: Enterprise E-Commerce Ecosystem

![CI Pipeline](https://github.com/Mayorman07/Micromart/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)

MicroMart is a high-availability, event-driven e-commerce backend built on a **Microservices Architecture**. It features 9 independent services orchestrated via Spring Cloud, synchronized through a shared security library, and integrated via RabbitMQ.

---

## 🏗️ System Architecture

This ecosystem follows the **API Gateway Pattern** and **Service Discovery Pattern** to ensure high scalability and loose coupling.

```mermaid
graph LR
    %% Color Styling Definitions
    classDef business fill:#0d6efd,color:#fff,stroke:#0a58ca,stroke-width:2px;
    classDef infra fill:#198754,color:#fff,stroke:#146c43,stroke-width:2px;
    classDef database fill:#ffc107,color:#000,stroke:#cc9a06,stroke-width:2px;
    classDef shared fill:#6f42c1,color:#fff,stroke:#59339d,stroke-width:2px;

    Client((Customer App)) --> Gateway[API Gateway: 7082]
    class Gateway business
    
    subgraph "🟩 Infrastructure"
        Config[Config Server: 7012]
        Eureka[Eureka Discovery: 7010]
        Rabbit[RabbitMQ Broker]
    end
    class Config,Eureka,Rabbit infra

    subgraph "🟦 Business Logic Services"
        Users[Users Service: 0]
        Products[Products Service: 7016]
        Cart[Cart Service: 7041]
        Order[Order Service: 7063]
        Inventory[Inventory Service: 7061]
        Payment[Payment Service: 7007]
        Notification[Notification Service: 7050]
    end
    class Users,Products,Cart,Order,Inventory,Payment,Notification business

    subgraph "🟨 Databases"
        UsersDB[(Users DB)]
        ProductsDB[(Products DB)]
        OrderDB[(Order DB)]
        PaymentDB[(Payment DB)]
        InventoryDB[(Inventory DB)]
    end
    class UsersDB,ProductsDB,OrderDB,PaymentDB,InventoryDB database

    subgraph "🟪 Shared Core"
        JWT[[JwtAuthorities Library]]
    end
    class JWT shared

    %% Network & Sync Routing
    Gateway -.-> Eureka
    Gateway --> Users & Products & Cart & Order
    Cart -->|Sync: Check Stock| Inventory
    Order -->|Sync: Request Link| Payment

    %% Async RabbitMQ Flow
    Order -.->|Publishes Event| Rabbit
    Payment -.->|Publishes Event| Rabbit
    Rabbit -.->|Consumes Event| Inventory & Cart & Notification & Order

    %% Database Connections
    Users --> UsersDB
    Products --> ProductsDB
    Order --> OrderDB
    Payment --> PaymentDB
    Inventory --> InventoryDB
    Cart --> OrderDB
```