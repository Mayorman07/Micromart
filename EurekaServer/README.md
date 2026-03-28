# 🔍 Eureka Discovery Server (MicroMart)

The **Eureka Discovery Server** is the central nervous system of the MicroMart ecosystem. It provides service registration and discovery, allowing microservices to locate and communicate with each other dynamically without hardcoded IP addresses or port numbers.

---

## 🚀 Core Responsibilities
* **Service Registration:** Maintains a real-time registry of all active microservice instances.
* **Health Monitoring:** Performs periodic heartbeats to ensure registered services are alive and healthy.
* **Load Balancing Support:** Provides the API Gateway and Feign Clients with the necessary metadata to perform client-side load balancing.
* **High Availability:** Configured to handle dynamic scaling where services (like the Users Service) may start on random ports (`0`).

---

## 🛠️ Tech Stack
* **Spring Cloud Netflix Eureka:** The industry-standard service discovery engine.
* **Spring Boot:** For rapid infrastructure bootstrapping.
* **Dashboard UI:** Built-in web console for real-time monitoring of service status.

---

## 🏗️ MicroMart System Topology

The following diagram visualizes how the Discovery Server sits at the heart of the platform, tracking every service across the infrastructure and business logic layers.

```mermaid
graph TD
    %% Global Styles
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
        Users[Users Service: Port 0]
        Products[Products Service: 7016]
        Cart[Cart Service: 7041]
        Order[Order Service: 7063]
        Inventory[Inventory Service: 7061]
        Payment[Payment Service: 7007]
        Notification[Notification Service: 7050]
    end
    class Users,Products,Cart,Order,Inventory,Payment,Notification business

    %% Relationships
    Users & Products & Cart & Order & Inventory & Payment & Notification -.->|Register| Eureka
    Gateway -.->|Resolve| Eureka
```

---

## 📡 Service Registry (Port Map)

| Service Name | Port | Type |
| :--- | :--- | :--- |
| **Eureka Server** | `7010` | Infrastructure |
| **Config Server** | `7012` | Infrastructure |
| **API Gateway** | `7082` | Edge / Routing |
| **Users Service** | `0` (Dynamic) | Business Logic |
| **Products Service**| `7016` | Business Logic |
| **Cart Service** | `7041` | Business Logic |
| **Inventory Service**| `7061` | Business Logic |
| **Order Service** | `7063` | Business Logic |
| **Notification** | `7050` | Business Logic |
| **Payment Service** | `7007` | Business Logic |

---

## 🔄 How it Works: The Heartbeat Mechanism

1.  **Registration:** When a service starts, it sends a POST request to Eureka on port `7010` with its IP, port, and health check URL.
2.  **Heartbeat:** Every 30 seconds, the service sends a "Renew" signal. If Eureka doesn't hear from a service for 90 seconds, it is automatically purged from the registry.
3.  **Discovery:** When the **API Gateway (7082)** receives a request for `/api/cart/**`, it asks Eureka for the location of the `Cart-Service`. Eureka provides the current IP/Port, and the Gateway routes the request.

---

## ⚙️ Configuration Notes
* **Self-Preservation:** In the event of a network glitch, Eureka enters "Self-Preservation" mode to prevent the accidental mass-deletion of healthy services.
* **Dashboard Access:** The registry can be viewed visually by navigating to `http://localhost:7010` in any web browser.