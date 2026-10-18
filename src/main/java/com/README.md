# Banking Management System

A REST API for a simple banking system, built with Java 21 and Spring Boot. Customers can register, log in, open accounts, deposit, withdraw, transfer money and view their transaction history. Every request is secured with JWT, and each customer can only access their own accounts.

## Tech Stack

- Java 21
- Spring Boot 4 (Spring Web MVC, Spring Data JPA, Spring Security)
- MySQL
- JWT authentication (Spring Security OAuth2 Resource Server, HS256)
- Hibernate / JPA
- Bean Validation
- Maven

## Features

- Customer registration with input validation and duplicate-email check
- Passwords hashed with BCrypt (never returned in responses)
- Login that returns a JWT token (expires after 60 minutes)
- Savings and current accounts with auto-generated 12-digit account numbers
- Deposit and withdraw, with an insufficient-balance check
- Money transfer between accounts inside one database transaction (all or nothing)
- Transaction history for every account
- Ownership checks: a customer cannot view or use another customer's account
- Clear JSON error messages with correct HTTP status codes

## Design Highlights

- `BigDecimal` for all money values (no floating-point errors)
- `@Transactional` transfers that roll back completely if anything fails
- Pessimistic row locking, and accounts locked in a fixed order to avoid deadlocks
- Stateless security: no sessions, only signed tokens
- Global exception handler for consistent error responses

## API Endpoints

### Public

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/customers/register` | Create a customer |
| POST | `/api/customers/login` | Log in and receive a JWT token |

### Protected (header: `Authorization: Bearer <token>`)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/accounts` | Create an account |
| GET | `/api/accounts/{accountNumber}` | View one account |
| GET | `/api/accounts/customer/{customerId}` | View your accounts |
| POST | `/api/accounts/{accountNumber}/deposit` | Deposit money |
| POST | `/api/accounts/{accountNumber}/withdraw` | Withdraw money |
| POST | `/api/transfers` | Transfer between accounts |
| GET | `/api/accounts/{accountNumber}/transactions` | Transaction history |

### Example: register

```json
{
    "name": "Ravi Kumar",
    "email": "ravi@example.com",
    "password": "Ravi@123",
    "phone": "9876543210"
}
```

### Example: transfer

```json
{
    "fromAccount": "153045739376",
    "toAccount": "631896344126",
    "amount": 50
}
```

## HTTP Status Codes

| Code | Meaning |
|---|---|
| 200 / 201 | Success |
| 400 | Invalid input |
| 401 | Missing, expired or invalid token, or wrong login |
| 403 | The account belongs to someone else |
| 404 | Account or customer not found |
| 409 | Email already registered |
| 422 | Insufficient balance |

## Run Locally

1. Install Java 21 and MySQL.
2. Create the database:

```sql
CREATE DATABASE banking_db;
```

3. Copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties`.
4. Open `application.properties` and set your MySQL password and a JWT secret of at least 32 characters.
5. Start the app:

```
./mvnw spring-boot:run
```

On Windows PowerShell, use `.\mvnw.cmd spring-boot:run`. The API runs at `http://localhost:8080`. Tables are created automatically on first start.

## Project Structure

```
src/main/java/com/banking/banking_system/
    config/        Security and JWT configuration
    controller/    REST controllers and global error handler
    dto/           Request and response objects
    entity/        JPA entities (Customer, Account, AccountTransaction)
    exception/     Custom exceptions
    repository/    Spring Data repositories
    service/       Business logic
    util/          Helper classes
```

## Possible Improvements

- Refresh tokens and logout
- Admin role and admin endpoints
- Unit and integration tests
- Swagger / OpenAPI documentation
- A web front end

## Author

Potnuru Ushapathi