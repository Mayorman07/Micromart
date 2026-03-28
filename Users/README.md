# 👤 User Service

The **User Service** acts as the centralized Identity and Access Management (IAM) provider for the MicroMart ecosystem. It handles user registration, secure authentication, role-based access control (RBAC), token lifecycle management, and triggers asynchronous marketing/security events.

---

## 🚀 Core Responsibilities
* **Identity Management:** Handles user registration, profile updates, and soft-deletions (deactivation).
* **Security & Authentication:** Generates JWT Access Tokens and handles secure, expiring Refresh Token rotation.
* **Role-Based Access Control:** Enforces strict method-level security (e.g., `@PreAuthorize("hasRole('ADMIN')")`).
* **Automated Retention:** Runs scheduled chron jobs to detect dormant users and trigger "We Miss You" reactivation campaigns.
* **Account Recovery:** Manages secure, token-based password reset flows.

---

## 🛠️ Tech Stack & Patterns
* **Spring Security & JWT:** Stateless authentication using cryptographically signed access tokens.
* **BCrypt Password Encoding:** Ensures passwords are never stored or transmitted in plain text.
* **Spring Scheduling (`@Scheduled`):** Runs automated daily batch jobs for user retention analysis.
* **RabbitMQ (Topic Exchanges):** Acts as a heavy producer, broadcasting lifecycle events (registration, password resets) to the Notification Service.

---

## 📡 API Documentation

### **Authentication & Token Management**

| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/users/create` | Register a new user (Status: INACTIVE). | `PUBLIC` |
| `GET` | `/api/v1/auth/verify` | Verify email address using a generated token. | `PUBLIC` |
| `POST` | `/users/refresh-token` | Exchange an unexpired Refresh Token for a new Access Token. | `PUBLIC` |

### **User Profile Management**

| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `PUT` | `/users/update` | Update identity fields and sync shipping addresses. | `USER` |
| `GET` | `/users/view/{email}` | View profile details. | `OWNER` |
| `POST` | `/users/{email}` | Soft-delete / deactivate a user account. | `ADMIN` / `OWNER` |
| `DELETE` | `/users/{email}` | Hard-delete a user from the registry. | `ADMIN` |
| `GET` | `/users/all` | Paginated and keyword-searchable user directory. | `ADMIN` |

### **Account Recovery Flow**

| Method | Endpoint | Description | Auth |
| :--- | :--- | :--- | :--- |
| `POST` | `/password-reset/request` | Initiates a password reset (triggers RabbitMQ event). | `PUBLIC` |
| `POST` | `/password-reset/reset` | Verifies the reset token and updates the BCrypt password. | `PUBLIC` |

---

## 🔐 Token Security Architecture

To protect against hijacked sessions, this service implements a **Refresh Token Rotation** pattern.

1.  **Access Tokens** are short-lived (e.g., 15 minutes) and are used for API requests.
2.  **Refresh Tokens** are long-lived (e.g., 15 days), stored securely in the database, and mapped to the user.
3.  When an Access Token expires, the client sends the Refresh Token to `/users/refresh-token`.
4.  The system validates the Refresh Token's expiry against the database (`verifyExpiration()`). If valid, a new Access Token is issued.

---

## 🔄 Asynchronous Architecture: Password Reset Flow

Instead of blocking the user's HTTP request while generating tokens and sending emails, this service uses an event-driven choreography to handle password resets instantly and securely.

```mermaid
sequenceDiagram
    participant User
    participant Auth Controller
    participant RabbitMQ (Attempt Queue)
    participant User Event Listener
    participant Database
    participant RabbitMQ (Email Queue)

    User->>Auth Controller: POST /password-reset/request (email)
    Auth Controller->>RabbitMQ (Attempt Queue): 1. Publish PasswordResetRequestEvent
    Auth Controller-->>User: 200 OK (Instant Response)
    
    RabbitMQ (Attempt Queue)->>User Event Listener: 2. Consume Event
    Note over User Event Listener: Verify User Exists
    User Event Listener->>User Event Listener: Generate Secure Token (15m expiry)
    User Event Listener->>Database: 3. Save Token to User Record
    
    User Event Listener->>RabbitMQ (Email Queue): 4. Publish PasswordResetEventDto (with Token)
    Note over RabbitMQ (Email Queue): Notification Service consumes this to send the actual email.
  ```

## ⏱️ Scheduled Background Jobs

To drive user engagement, the service analyzes login patterns in the background:

* **Job:** `ReactivationScheduler.sendWeMissYouEmails()`
* **Schedule:** Every day at `10:00 AM` (`cron = "0 0 10 * * ?"`).
* **Action:** Queries the database for users who have been inactive between 30 and 60 days.
* **Result:** Generates a list of dormant users and publishes a `ReactivationEvent` to RabbitMQ for the Notification Service to process.


## 📨 Event-Driven Integration (RabbitMQ)

The User Service is the primary source of identity events. It utilizes a `TopicExchange` (`user.exchange`) to broadcast state changes.

### 📤 Published Events (Producer)

| Queue | Routing Key | Payload | Description |
| :--- | :--- | :--- | :--- |
| `user.notification.queue` | `user.created` | `UserCreatedEventDto` | Triggers the dispatch of a Welcome/Email Verification email. |
| `password-reset-attempt-queue`| `password.reset.attempt` | `PasswordResetRequestEvent` | Queues the request to generate a secure reset token. |
| `password.reset.queue` | `password_reset_routing_key`| `PasswordResetEventDto` | Triggers the dispatch of the actual Password Reset email with the token. |
| `user.reactivation.queue` | `user.reactivation` | `ReactivationEvent` | Triggers a "We Miss You" marketing email for dormant accounts. |

*(Note: All queues are configured with Dead Letter Exchanges (DLX) to ensure messages are not lost during outages).*

---

