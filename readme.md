# RentVideo - Online Video Rental System

A Spring Boot RESTful API for managing an online video rental system. Users can view and rent videos, while admins can manage the video catalog.

---

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Setup](#setup)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Example Requests & Responses](#example-requests--responses)
- [Testing](#testing)
- [Notes](#notes)
- [Author](#author)

---

## Features

- View available videos
- Rent and return videos
- Track user rentals
- Admin operations: add, update, delete videos
- JWT-based authentication and role-based access control
- Validation to prevent overbooking and duplicate rentals

---

## Technologies

- Java 21
- Spring Boot 3
- Spring Security (JWT)
- Spring Data JPA
- H2 Database / MySQL (configurable)
- Gradle
- JUnit & MockMvc for testing

---

## Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/<your-username>/RentVideo.git
   cd RentVideo
   ```

2. Configure application properties (`src/main/resources/application.properties`):

   ```properties
   spring.datasource.url=jdbc:h2:mem:rentvideo
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   spring.jpa.hibernate.ddl-auto=update
   spring.h2.console.enabled=true
   jwt.secret=your_jwt_secret_here
   ```

3. Build the project:

   ```bash
   ./gradlew build
   ```

---

## Running the Application

Start the Spring Boot server:

```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`.

---

## API Endpoints

### **Videos**

| Method | Endpoint              | Description               | Role             |
| ------ | --------------------- | ------------------------- | ---------------- |
| GET    | `/videos`             | List all available videos | Customer / Admin |
| GET    | `/videos/my-rentals`  | List your active rentals  | Customer         |
| POST   | `/videos`             | Add a new video           | Admin            |
| PUT    | `/videos/{id}`        | Update a video            | Admin            |
| DELETE | `/videos/{id}`        | Delete a video            | Admin            |
| POST   | `/videos/{id}/rent`   | Rent a video              | Customer         |
| POST   | `/videos/{id}/return` | Return a video            | Customer         |

---

## Authentication

- JWT-based authentication
- Roles: `CUSTOMER`, `ADMIN`
- Customers cannot add, update, or delete videos
- Admins can manage videos but cannot rent them

---

## Example Requests & Responses

### **Rent a Video**

**Request:**

```http
POST /videos/1/rent
Authorization: Bearer <JWT_TOKEN>
```

**Response (success):**

```json
{
  "message": "Video 'Inception' rented successfully"
}
```

**Response (error if already rented):**

```json
{
  "error": "Video 'Inception' is already rented by john@example.com"
}
```

**Response (error if user has 2 active rentals):**

```json
{
  "error": "You already have 2 active rentals."
}
```

### **Return a Video**

**Request:**

```http
POST /videos/1/return
Authorization: Bearer <JWT_TOKEN>
```

**Response (success):**

```json
{
  "message": "Video 'Inception' returned successfully"
}
```

### **Get User Rentals**

**Request:**

```http
GET /videos/my-rentals
Authorization: Bearer <JWT_TOKEN>
```

**Response:**

```json
[
  {
    "id": 1,
    "title": "Inception",
    "director": "Christopher Nolan",
    "genre": "Sci-Fi",
    "available": false
  }
]
```

### **Admin Add Video**

**Request:**

```http
POST /videos
Authorization: Bearer <ADMIN_JWT>
Content-Type: application/json

{
  "title": "Interstellar",
  "director": "Christopher Nolan",
  "genre": "Sci-Fi",
  "available": true
}
```

**Response:**

```json
{
  "id": 2,
  "title": "Interstellar",
  "director": "Christopher Nolan",
  "genre": "Sci-Fi",
  "available": true
}
```

---

## Testing

- Unit tests are provided under `src/test/java/com/rentvideo`
- Run tests with:
  ```bash
  ./gradlew test
  ```

Tests cover:

- Renting restrictions (max 2 active rentals per user)
- Video availability
- Role-based endpoint access
- CRUD operations for Admin

---

## Notes

- Video availability is automatically updated when rented or returned
- H2 in-memory database is used by default; can be replaced with MySQL or PostgreSQL
- JWT expiration and secret are configurable in `application.properties`

---

## Author

Shushant Rishav
