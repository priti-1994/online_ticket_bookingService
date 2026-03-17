# High-Level Design (HLD) - Ticket Booking Service

1. Purpose

This document provides a high-level architecture overview for the Ticket Booking Service implemented in this repository. It explains components, public APIs, data flow for the booking use-case, concurrency model, scalability considerations, and non-functional requirements.

2. System Overview

The Ticket Booking Service is a Spring Boot monolith that offers:
- Browse: search movies and shows by language, genre, or city.
- Book: reserve and confirm seats for a show with discount calculation.

Core technologies:
- Java 21 + Spring Boot (REST controllers, Spring Data JPA)
- Relational database (JPA/Hibernate)
- Optional Redis (for distributed seat locks)

3. Public APIs (contract)

- POST /bookings
  - Request JSON (BookingRequest):
    - shows_Id: long (id of the show)
    - seats: string[] (list of seat numbers, e.g. ["A1","A2"]) 
  - Responses:
    - 201 Created: BookingResponse { booking_Id: long, totalPrice: double, status: string }
    - 400 Bad Request: invalid payload
    - 404 Not Found: show not found
    - 409 Conflict: seat already booked or locked

- GET /browse/language/{language}
  - 200: list of Movie
  - 404: Movies in language 'Arabic' are not available.

- GET /browse/genre/{genre}
  - 200: list of Movie
  - 404: Movies in genre 'drama' are not available.

- GET /browse/city/{city}
  - 200: list of Shows
  - 404: Movies in city 'Goa' are not available.

4. Main Components

- Controllers
  - BookingController - accepts booking requests and returns 'BookingResponse'.
  - BrowseController - read-only endpoints for movies/shows.

- Services
  - BookingService - transactional booking flow: validate seats, mark BOOKED, compute price & discounts, persist `Booking` and 'BookingSeats'.
  - BrowseService - read operations.
  - Optional SeatLockService - acquire short-lived Redis locks per seat when enabled.

- Repositories (Spring Data JPA)
  - 'MovieRepo', 'ShowsRepo', 'SeatsRepo', 'BookingsRepo', 'BookingSeatsRepo'

- Domain
  - Entities: 'Movie', 'Theatre', 'Shows', 'Seats', 'Booking', 'BookingSeats'.
  - Enums: 'SeatStatus' (AVAILABLE, BOOKED), 'BookingStatus' (CONFIRMED, CANCELLED, ...)

- Discounts
  - 'DiscountStrategy' implementations combined by 'DiscountFactory' to compute final price.

5. Booking Flow (high level)

Sequence (happy path):
1. Client POST /bookings with 'shows_Id' and 'seats' list.
2. Controller calls 'BookingService.bookTickets(request)'.
3. Service loads 'Shows' and requested 'Seats' (query by 'shows_Id' + seat numbers).
4. Validate results: requested count matches found rows; all seats are AVAILABLE.
5. Optionally: acquire Redis locks per-seat to reduce contention across instances.
6. Mark seats as BOOKED and persist ('SeatsRepo.saveAll') inside a DB transaction.
7. Compute base price = show.price * ticketCount.
8. Call 'DiscountFactory.applyDiscount(basePrice, ticketCount, show)' to get final price.
9. Persist 'Booking' and 'BookingSeats' mappings.
10. Release any acquired locks and return 'BookingResponse'.

6. Concurrency & Consistency Model

- DB transaction (single '@Transactional' boundary) guarantees atomicity of seat updates and booking creation on a single instance.
- Optimistic locking: 'Seats' entity contains a '@Version' field so concurrent updates cause 'OptimisticLockException' and rollback. The app may retry on such exceptions (recommended).

7. Non-functional Requirements

- Consistency: booking is ACID within a transaction.
- Availability: aim for horizontal scaling and health checks.

8. Future Enhancements

- Horizontal scaling: multiple instances behind a load balancer; use Redis distributed locks to coordinate seat reservations.
- Read traffic: cache browse results via Redis or CDN, and consider DB read-replicas.
- Resilience: circuit-breakers for downstream systems (e.g., payment gateway), connection pooling, timeouts, and retries for transient DB/Redis errors.

- Metrics: booking rate, conflict rate, lock acquisition rate, retry counts.
- Structured logging with contextual fields (request id, show id, seat ids).
- Tracing (OpenTelemetry) for end-to-end request latency.

- Add retries with exponential backoff for optimistic lock failures.
- Integrate payment flow (pre-authorize then capture) and decouple via events (outbox pattern).
- Add seat hold (temporary reservation) with background expiration.
- Publish booking events (Kafka) for downstream consumers.

9. References

- Sequence diagram: ./sequence_diagram.svg
- Low-level design: ./LLD.md
- Key classes: 'BookingService', 'DiscountFactory'.