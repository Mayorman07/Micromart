# 🛒 MicroMart: Enterprise E-Commerce Ecosystem

![CI Pipeline](https://github.com/Mayorman07/Micromart/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-purple)
![Event-Driven](https://img.shields.io/badge/Pattern-Event%20Driven-yellow)

MicroMart is an enterprise-grade, distributed e-commerce ecosystem built on a reactive, event-driven architecture. Designed for high availability and elastic scalability, the platform demonstrates sophisticated patterns including distributed transactions (Saga), reactive data streams, and automated resilience.
---
> **🏆 Roadmap.sh Project:** This repository is my official solution for the [Scalable E-Commerce Platform](https://roadmap.sh/projects/scalable-ecommerce-platform) architecture challenge.
---
## 🏗️ System Architecture

This ecosystem follows the **API Gateway Pattern**, **Service Discovery Pattern**, and the **Database-per-Service Pattern** to ensure high scalability and loose coupling.

```mermaid
graph LR
    %% Color Styling Definitions
    classDef business fill:#0d6efd,color:#fff,stroke:#0a58ca,stroke-width:2px;
    classDef infra fill:#198754,color:#fff,stroke:#146c43,stroke-width:2px;
    classDef database fill:#ffc107,color:#000,stroke:#cc9a06,stroke-width:2px;
    classDef shared fill:#6f42c1,color:#fff,stroke:#59339d,stroke-width:2px;
    classDef external fill:#ffffff,color:#000,stroke:#cccccc,stroke-width:2px;

    Client((Customer App)) --> Gateway[API Gateway: 7082]
    class Client external
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

    subgraph "🟪 Cross-Cutting Concerns"
        JWT[["📦 JwtAuthorities.jar <br/> (Embedded in Services)"]]
    end
    class JWT shared

    %% Network & Sync Routing
    Gateway -.-> Eureka
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

### 📊 Diagram Legend

| Shape & Color          | Node Type            | Description                                                        |
| :--------------------- | :------------------- | :----------------------------------------------------------------- |
| ⚪ **White Circle** | **External Actor** | The end-user client (Mobile/Web App).                              |
| 🟦 **Blue Rectangle** | **Business Service** | Independent microservices handling core domain logic.              |
| 🟩 **Green Rectangle** | **Infrastructure** | Backbone services supporting the ecosystem (Routing, Messaging).   |
| 🟨 **Yellow Cylinder** | **Database** | Isolated persistence layers (Database-per-Service pattern).        |
| 🟪 **Purple Box** | **Shared Library** | Reusable `.jar` dependencies embedded at compile-time.             |

**Communication Lines:**
* `───>` **Solid Line:** Synchronous HTTP/REST Call (Blocking)
* `- - ->` **Dotted Line:** Asynchronous Message / Event-Driven Flow (Non-Blocking)

---

## 🔄 The Transaction Lifecycle

When a user places an order, the following distributed transaction occurs across the ecosystem:

```mermaid
sequenceDiagram
    participant User
    participant Gateway
    participant Auth as Users (JWT)
    participant Order
    participant Payment
    participant Rabbit as RabbitMQ (Broker)
    participant Inventory
    participant Notify as Notification

    User->>Gateway: POST /api/v1/orders/checkout
    Gateway->>Auth: Validate JWT Token
    Auth-->>Gateway: Token Valid (Role: USER)
    Gateway->>Order: Create Order #99 (Status: PENDING)
    Order->>Payment: Request Payment Session
    Payment-->>User: Redirect to Payment Gateway
    
    Note over Payment: User completes transaction
    
    Payment->>Rabbit: Publish: PaymentSuccessEvent
    
    par Async Consumers
        Rabbit-->>Order: Update Status to PAID
        Rabbit-->>Inventory: Deduct Stock
        Rabbit-->>Notify: Send Confirmation Email
    end
```

---

## 🛡️ Resilience & Observability

MicroMart is built with a **"Design for Failure"** mindset. The API Gateway serves as a resilient entry point using:

* **Circuit Breakers (Resilience4J):** Configured with a state-machine for high-risk routes (like `Users`).
    * **Trip Logic:** If the failure rate hits **50%** over a rolling window of **10 calls**, the circuit opens to halt traffic.
    * **Self-Healing (Half-Open):** After a **10-second wait time**, the Gateway allows exactly **3 test requests** through. If they succeed, the circuit closes and normal traffic resumes; if they fail, it trips open again.
* **Time Limiting:** Strict **5-second timeouts** ensure that a hanging downstream service does not exhaust the Gateway's thread pool.
* **Global CORS:** Securely configured for modern frontend integration (e.g., React on port 3000).
* **Observability:** Integrated with **Spring Boot Actuator** for real-time health checks and metric gathering.

---

## 📦 Service Registry

| Service | Primary Responsibility | Port   |
| :--- | :--- |:-------|
| **Gateway** | Unified entry point, routing, and load balancing | `7082` |
| **ConfigServer** | Centralized configuration management | `7012` |
| **EurekaServer** | Service registration and dynamic discovery | `7010` |
| **Users** | Identity management and RBAC (Role-Based Access Control) | `0`    |
| **Products** | Catalog management and product metadata | `7016` |
| **Cart** | Real-time shopping cart persistence | `7041` |
| **Order** | Transaction orchestration and checkout flow | `7063` |
| **Inventory** | Stock tracking and safety-stock logic | `7061` |
| **Payment** | Transaction processing and billing history | `7007` |
| **Notification** | Multi-channel messaging (Email/SMS) via RabbitMQ | `7050` |
| **JwtAuthorities** | **[Library]** Reusable security filters and token logic | `N/A`  |

---

## 🚀 Local Development Setup

### 1. Install Shared Library
Because `JwtAuthorities` is a custom internal library, it must be installed to your local `.m2` repository first:
```bash
cd JwtAuthorities && mvn clean install
```

### 2. Build All Services
```bash
mvn clean package -DskipTests
```

### 3. Run via Docker Compose
```bash
docker-compose up --build
```