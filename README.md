
[`AbstractUser.java`](src/main/java/com/hotel/model/AbstractUser.java) is the common mapped superclass for user types:

```text
AbstractUser
├── Customer
├── HotelOwner
└── User
```

Common fields such as `id`, `name`, `email` and `password` are defined once in the parent class and inherited by the child classes.

### 3. Polymorphism

- **Method overriding:** `Customer`, `HotelOwner` and `User` provide their own implementation of the abstract `getRole()` method.
- **Method overloading:** [`HotelService.java`](src/main/java/com/hotel/service/HotelService.java) contains:
  - `getHotels()` to retrieve all hotels.
  - `getHotels(Long ownerId)` to retrieve hotels belonging to one owner.

CRUD means:

- **Create:** Add a new record.
- **Read:** Retrieve and display records.
- **Update:** Change an existing record.
- **Delete:** Remove or cancel a record.

The project implements complete CRUD flows for the three assigned modules below.

---

## Member 1: Hotel Property Management

### Responsibility

Member 1 manages hotel property listings owned by a hotel owner. A property includes:

- Hotel name
- Location
- Price per night
- Description
- Image URL or uploaded image
- Owner ID

### Class files

| Layer | File | Responsibility |
|---|---|---|
| Model | [`Hotel.java`](src/main/java/com/hotel/model/Hotel.java) | Hotel entity and property fields |
| Repository | [`HotelRepository.java`](src/main/java/com/hotel/repository/HotelRepository.java) | Hotel persistence and owner-based lookup |
| Service | [`HotelService.java`](src/main/java/com/hotel/service/HotelService.java) | Hotel validation and business logic |
| Controller | [`OwnerController.java`](src/main/java/com/hotel/controller/OwnerController.java) | Owner dashboard and hotel CRUD endpoints |
| Configuration | [`WebResourceConfig.java`](src/main/java/com/hotel/config/WebResourceConfig.java) | Serves uploaded hotel images |
| View | [`owner_dashboard.html`](src/main/resources/templates/owner_dashboard.html) | Add, edit, delete and view properties |
| View | [`index.html`](src/main/resources/templates/index.html) | Public hotel listings |

### CRUD mapping

| Operation | Endpoint | Description |
|---|---|---|
| Create | `POST /owner/add-hotel` | Creates a property for the logged-in owner |
| Read | `GET /owner/dashboard` | Displays the owner's properties |
| Read | `GET /` | Displays public hotel listings |
| Update | `POST /owner/update-hotel` | Updates property details |
| Delete | `POST /owner/delete-hotel` | Deletes a property |

### Property image upload

The owner may enter an image URL or upload an image from the local computer.

- Form field: `imageFile`
- Accepted content types: `image/*`
- New files are saved under `uploads/hotels`.
- The database stores a URL such as `/uploads/hotels/<generated-file-name>`.
- [`WebResourceConfig.java`](src/main/java/com/hotel/config/WebResourceConfig.java) maps `/uploads/hotels/**` to the upload directory.
- Existing legacy files under `src/main/resources/static/uploads/hotels` are also supported.

### Business rules

- Only an authenticated user with role `OWNER` can access the owner dashboard.
- The minimum nightly price is LKR 1,000.
- Owner properties are loaded using the current owner's ID.
- Uploaded files must have an image MIME type.

## Member 2: Reservation & Booking Management

### Responsibility

Member 2 manages the complete reservation lifecycle:

- Search and view available hotel listings
- Create reservations
- View current and previous reservations
- Reschedule reservations
- Cancel reservations
- Calculate total price using nightly price, date range and guest count

### Class files

| Layer | File | Responsibility |
|---|---|---|
| Model | [`Booking.java`](src/main/java/com/hotel/model/Booking.java) | Booking entity, dates, guests, price and status |
| Repository | [`BookingRepository.java`](src/main/java/com/hotel/repository/BookingRepository.java) | Booking persistence and queries |
| Service | [`HotelService.java`](src/main/java/com/hotel/service/HotelService.java) | Booking creation, validation, update and cancellation |
| Controller | [`CustomerController.java`](src/main/java/com/hotel/controller/CustomerController.java) | Customer booking endpoints |
| View | [`customer_dashboard.html`](src/main/resources/templates/customer_dashboard.html) | Booking forms, booking list and rescheduling modal |
| View | [`index.html`](src/main/resources/templates/index.html) | Public hotel booking entry point |

### CRUD mapping

| Operation | Endpoint | Description |
|---|---|---|
| Create | `POST /customer/book` | Creates a reservation |
| Read | `GET /customer/dashboard` | Displays the customer's bookings |
| Update | `POST /customer/update-booking` | Reschedules dates or changes guest count |
| Delete / Cancel | `POST /customer/cancel-booking` | Cancels a reservation |

### Booking business rules

- Only the logged-in customer can update or cancel their own booking.
- Check-in and check-out dates must be valid.
- Check-out must be after check-in.
- Guest count must be at least one.
- Dates must satisfy the application's future-date rules.
- Overlapping active bookings are rejected.
- The booking stores the guest count and total price at booking time.
- Booking status values include `BOOKED` and `CANCELLED`.
- This project models availability at hotel level; it does not contain a separate room entity.

## Member 3: Review & Rating Management

### Responsibility

Member 3 manages customer feedback for hotel properties:

- Add a review after a stay or booking
- View personal reviews
- View reviews associated with a hotel
- Edit review rating or comment
- Delete a review

### Class files

| Layer | File | Responsibility |
|---|---|---|
| Model | [`Review.java`](src/main/java/com/hotel/model/Review.java) | Review entity, rating validation and review date |
| Repository | [`ReviewRepository.java`](src/main/java/com/hotel/repository/ReviewRepository.java) | Review persistence and hotel/customer queries |
| Service | [`ReviewService.java`](src/main/java/com/hotel/service/ReviewService.java) | Review CRUD and ownership rules |
| Controller | [`ReviewController.java`](src/main/java/com/hotel/controller/ReviewController.java) | Review endpoints |
| View | [`customer_dashboard.html`](src/main/resources/templates/customer_dashboard.html) | Customer review form and personal reviews |
| View | [`owner_dashboard.html`](src/main/resources/templates/owner_dashboard.html) | Reviews shown to hotel owners |

### CRUD mapping

| Operation | Endpoint | Description |
|---|---|---|
| Create | `POST /review/add` | Adds a 1–5 star review and comment |
| Read | `GET /customer/dashboard` | Displays the customer's reviews |
| Read | Owner dashboard | Displays reviews for each owned hotel |
| Update | `POST /review/update` | Updates rating or comment |
| Delete | `POST /review/delete` | Deletes a review |

### Review business rules

- Rating values are limited to 1–5.
- The review date is recorded automatically.
- A customer can manage only their own reviews.
- Owners can see reviews belonging to their hotel properties.
- Review data includes customer ID, customer name, hotel ID, hotel name, rating, comment and date.

## Authentication and Role Management

Authentication is handled by [`AuthController.java`](src/main/java/com/hotel/controller/AuthController.java).

| Feature | Endpoint |
|---|---|
| Public home page | `GET /` |
| Login page | `GET /login` |
| Login submission | `POST /login` |
| Registration page | `GET /register` |
| Registration submission | `POST /register` |
| Logout | `GET /logout` |

The current user is stored in the HTTP session. Controllers check the user's role before allowing access to owner or customer operations.

## Database Entities

| Entity | Table | Main fields |
|---|---|---|
| `User` | `users` | id, name, email, password, role |
| `Customer` | `customers` | inherited user fields |
| `Hotel` | `hotels` | name, location, price, owner, image, description |
| `Booking` | `bookings` | customer, hotel, dates, guests, total price, status |
| `Review` | `reviews` | customer, hotel, rating, comment, review date |

The SQL Server recreation script is available at [`database/create-hotelreservationdb.sql`](database/create-hotelreservationdb.sql). It creates the database schema and seed data for development/testing.

## Database Configuration

The active configuration is in [`application.properties`](src/main/resources/application.properties). The current project configuration targets SQL Server:

```properties
server.port=8080
spring.datasource.url=jdbc:sqlserver://localhost:1177;databaseName=HotelReservationDB
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

### SQL Server / SSMS setup

1. Start the SQL Server service.
2. Open SSMS and connect to the configured SQL Server instance.
3. Execute [`database/create-hotelreservationdb.sql`](database/create-hotelreservationdb.sql).
4. Configure the credentials as environment variables:

```powershell
$env:DB_USERNAME = "sa"
$env:DB_PASSWORD = "your-sql-server-password"
```

5. If the SQL Server port is different, update the URL in `application.properties` or use the SQL Server profile in [`application-sqlserver.properties`](src/main/resources/application-sqlserver.properties).
6. Refresh `HotelReservationDB > Tables` in SSMS after starting the application.

The web application and SSMS must use the same database configuration. If the application runs against H2 while SSMS shows SQL Server, web changes will not appear in SSMS.

## How to Run

### Windows

```powershell
cd D:\projects\HotelReservationSystem\HotelReservationSystem
.\mvnw.cmd clean spring-boot:run
```

If using the SQL Server profile:

```powershell
$env:DB_USERNAME = "sa"
$env:DB_PASSWORD = "your-sql-server-password"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=sqlserver"
```

Then open:

```text
http://localhost:8080
```

### Demo accounts

The database script provides sample accounts for testing. Confirm the exact seeded credentials in [`database/create-hotelreservationdb.sql`](database/create-hotelreservationdb.sql).

Typical development accounts are:

| Role | Email | Password |
|---|---|---|
| Customer | `customer@hotel.com` | `123456` |
| Owner | `owner@hotel.com` | `123456` |

## Testing and Validation

Compile the project:

```powershell
.\mvnw.cmd -DskipTests compile
```

Run the test suite:

```powershell
.\mvnw.cmd test
```

Booking regression tests are located in [`HotelServiceBookingTests.java`](src/test/java/com/hotel/service/HotelServiceBookingTests.java). They verify booking dates, guest count and total price persistence after an update.

## Important Project Files

| File | Purpose |
|---|---|
| [`pom.xml`](pom.xml) | Maven dependencies and Spring Boot build configuration |
| [`application.properties`](src/main/resources/application.properties) | Active application and database settings |
| [`application-sqlserver.properties`](src/main/resources/application-sqlserver.properties) | SQL Server profile settings |
| [`database/create-hotelreservationdb.sql`](database/create-hotelreservationdb.sql) | SQL Server schema and seed data |
| [`style.css`](src/main/resources/static/css/style.css) | Light theme, responsive layout and UI styling |
| [`index.html`](src/main/resources/templates/index.html) | Public hotel listing page |
| [`owner_dashboard.html`](src/main/resources/templates/owner_dashboard.html) | Owner property and review management |
| [`customer_dashboard.html`](src/main/resources/templates/customer_dashboard.html) | Customer bookings and reviews |

## Summary for Viva Presentation

### Member 1

“I implemented hotel property management. My module uses `Hotel`, `HotelRepository`, `HotelService` and `OwnerController`. It supports complete CRUD operations, owner-specific listings, price validation and hotel image uploads.”

### Member 2

“I implemented reservation and booking management. My module uses `Booking`, `BookingRepository`, `HotelService` and `CustomerController`. It validates dates and availability, calculates total prices, and supports create, read, reschedule and cancel operations.”

### Member 3

“I implemented review and rating management. My module uses `Review`, `ReviewRepository`, `ReviewService` and `ReviewController`. It supports 1–5 ratings, comments, automatic review dates, ownership validation and complete CRUD operations.”
