# Product Management REST API

A production-ready Product Management System built with Spring Boot 3, featuring JWT authentication with Refresh Token rotation, JPA Auditing, and containerization.

This project was developed as part of a backend technical evaluation, with focus on clean architecture, secure authentication, and production-readiness.

## Tech Stack

- **Java 21 LTS**
- **Spring Boot 3.2.2**
- **Spring Data JPA**
- **Spring Security** (JWT + Refresh Token Rotation)
- **PostgreSQL** (Production) / **H2** (Test)
- **MapStruct** for DTO mapping
- **Springdoc-OpenAPI** (Swagger)
- **Docker & Docker Compose**
- **JUnit 5 & Mockito**

## Architecture Analysis

The project is structured following the **Controller-Service-Repository** pattern, which provides a clear separation of concerns, making the application maintainable and testable.

### Package-by-Feature Structure
- **`config`**: Contains infrastructure configurations (Security, JPA Auditing, OpenAPI).
- **`controller`**: Handles REST requests, input validation, and maps to standardized API responses.
- **`service`**: encapsulates business logic. Every modification is wrapped in a `@Transactional` boundary to ensure atomicity.
- **`repository`**: Uses Spring Data JPA for abstractions over SQL queries.
- **`security`**: Implements the JWT filter chain and token management.
- **`entity`**: Defines the data model with indexing strategies as required by the schema.

### Key Performance & Security Decisions
- **Database Indexing**: Indexes are explicitly defined on `product_name` and `product_id` (foreign key) in the JPA entities to optimize lookup performance.
- **Pagination & Sorting**: Implemented for the product list endpoint using Spring Data JPA's `Pageable`. This allows clients to handle large datasets efficiently.
- **Refresh Token Rotation**: Implemented using an update-if-exists approach. This avoids duplicate token entries and ensures only one active refresh session per user.
- **Async Execution**: A sample async method (simulateSlowExport) is included to demonstrate handling of long-running operations without blocking the main request thread.
- **Role-based Authorization**: `ADMIN` and `USER` roles are enforced using Spring Security annotations (e.g., `@PreAuthorize`) to protect sensitive endpoints.
- **Input Validation**: Robust DTO validation using **Jakarta Validation** ensures request data integrity and prevents malicious input.
- **CORS Configured**: Securely allows controlled cross-origin access, essential for modern frontend integration.
- **Global Error Handling**: A `@RestControllerAdvice` ensures that even security exceptions (like `AccessDenied`) are returned as valid JSON objects instead of standard Spring error pages.

## Architecture

The project follows a clean layered structure to keep responsibilities separated and the codebase maintainable:

- `controller`: REST Endpoints and API documentation.
- `service`: Core business logic and transaction management.
- `repository`: Data access layer using JPA.
- `entity`: Database models and JPA Auditing.
- `dto`: Data Transfer Objects for API request/response.
- `mapper`: MapStruct interfaces for Entity-DTO conversion.
- `security`: JWT filters, Token providers, and Security configuration.
- `exception`: Global exception handling and custom error responses.

## Setup Instructions

### Prerequisites
- JDK 21 LTS
- Maven 3.8+
- Docker & Docker Compose (optional)

### Running Locally
1. Clone the repository.
2. Configure PostgreSQL credentials in `src/main/resources/application.yml` (dev profile).
3. Run the application:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

### Running with Docker
```bash
docker-compose up --build
```

Note: Ensure that port 8080 and database ports are not already in use on your system before starting containers.

## Security Flow

1. **Registration**: `POST /api/v1/auth/register`
2. **Login**: `POST /api/v1/auth/login` returns `accessToken` (15m) and `refreshToken` (7d).
3. **Protected Requests**: Include `Bearer <accessToken>` in the `Authorization` header.
4. **Token Refresh**: `POST /api/v1/auth/refresh` with `refreshToken`.
   - **Rotation**: On every refresh, a NEW `refreshToken` is issued, and the OLD one is invalidated. This prevents replay attacks.

## API Documentation
Once the app is running, access Swagger UI at:
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Testing
The project includes unit and integration tests. Run them using:
```bash
mvn test
```

## Commit Message Suggestions
- `feat: implement jwt refresh token rotation logic`
- `feat: add product crud with pagination and sorting`
- `fix: handle lazy loading in item retrieval`
- `refactor: move to constructor injection for all services`

## HTTPS & Production Considerations

While HTTPS is critical in production environments, this project runs over HTTP in local and Dockerized environments for simplicity.

In real-world deployments, HTTPS enforcement should be handled at infrastructure level such as:

- Nginx reverse proxy
- Cloud Load Balancer (AWS ALB, Azure Front Door, etc.)
- API Gateway

Spring Boot application typically runs behind such reverse proxy and communicates internally over HTTP.

This design ensures:
- Secure encrypted communication externally
- Simplified container configuration
- Separation of infrastructure and application responsibilities

## Assumptions & Decisions
- **Async Export**: Simulated using `@Async` to show handling of long-running tasks.
- **Single Session**: Refresh token rotation is implemented to allow only one active refresh token per user at a time.
- **Auditing**: JPA Auditing captures `SYSTEM` if no user is authenticated (e.g., during registration).

---
**Time Taken**: ~7 hours (Design, Implementation, Security hardening, and Testing).
