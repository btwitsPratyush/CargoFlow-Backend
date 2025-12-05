# Transport Management System (TMS) Backend

A production-grade backend for a Transport Management System built with Spring Boot 3.2, Java 17, and PostgreSQL.

## Features
- **Load Management**: Post, search, and cancel loads.
- **Transporter Management**: Register carriers and manage truck capacity.
- **Bidding System**: Competitive bidding with a "Best Bid" scoring algorithm.
- **Booking Management**: Automated booking with capacity checks and concurrency control.
- **Business Rules**:
    - Capacity validation before booking.
    - State machine for Load status (POSTED -> OPEN -> BOOKED).
    - Optimistic locking to prevent double booking.

## Tech Stack
- **Java 17**
- **Spring Boot 3.2.3** (Web, Data JPA, Validation)
- **PostgreSQL**
- **Lombok**
- **Springdoc OpenAPI** (Swagger UI)

## Setup Instructions

### Prerequisites
- JDK 17+
- Maven 3.8+
- PostgreSQL 14+

### Database Setup
1. Create a PostgreSQL database named `tms_db`.
2. Update `src/main/resources/application.yml` with your database credentials if different from default (`postgres`/`password`).

### Build and Run
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The application will start on port `8080`.

## API Documentation
Swagger UI is available at: `http://localhost:8080/swagger-ui.html`

### Key Endpoints

#### Transporters
- `POST /api/v1/transporters` - Register a new transporter
- `PUT /api/v1/transporters/{id}/trucks` - Update truck capacity

#### Loads
- `POST /api/v1/loads` - Post a new load
- `GET /api/v1/loads/{id}/best-bids` - Get ranked bids for a load

#### Bids
- `POST /api/v1/bids` - Submit a bid for a load

#### Bookings
- `POST /api/v1/bookings` - Accept a bid and create a booking

## Business Logic Details

### Best Bid Algorithm
Bids are scored based on rate (lower is better) and transporter rating (higher is better):
`Score = (1 / Rate) * 0.7 + (Rating / 5.0) * 0.3`

### Concurrency Control
The system uses JPA `@Version` for optimistic locking on the `Load` entity. If two transporters try to book the last remaining trucks simultaneously, one will succeed and the other will receive a `409 Conflict` error.

## Testing
Run unit tests with:
```bash
mvn test
```
