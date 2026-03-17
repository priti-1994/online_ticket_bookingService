# Low-Level Design (LLD) - Ticket Booking Service

1. Purpose

This document maps the implemented code to concrete data structures, class contracts, transaction flows, concurrency controls, and recommended improvements.

2. Public DTOs (contracts)

- BookingRequest (used by POST /bookings)
  - shows_Id: long
  - seats: List<String> (seat numbers such as "A1")
  - Example (JSON):
    {
      "shows_Id": 123,
      "seats": ["A1","A2"]
    }

- BookingResponse
  - booking_Id: long
  - totalPrice: double
  - status: string (BookingStatus enum)
  - Example:
    {
      "booking_Id": 987,
      "totalPrice": 180.0,
      "status": "CONFIRMED"
    }

3. Key Classes & Method Signatures (code mapping)

- BookingController
  - Endpoint: POST /bookings
  - Calls: 'BookingService.bookTickets(BookingRequest)'

- BookingService (src/main/java/.../service/BookingService.java)
  - Constructor-injected dependencies: 'BookingsRepo', 'ShowsRepo', 'SeatsRepo', 'BookingSeatsRepo', 'DiscountFactory'.
  - Method: '@Transactional public BookingResponse bookTickets(BookingRequest bookingRequest)'
    - Loads 'Shows show = showsRepo.findById(bookingRequest.shows_Id()).orElseThrow(...)'.
    - Loads 'List<Seats> seats = seatsRepo.findByShows_IdAndSeatNumberIn(bookingRequest.shows_Id(), bookingRequest.seats())'.
    - Validates size equality and each seat's status.
    - Updates seat status: 'seat.setStatus(SeatStatus.BOOKED)` and 'seatsRepo.saveAll(seats)'.
    - Computes basePrice and calls 'discountFactory.applyDiscount(basePrice, seats.size(), show)'.
    - Persists 'Booking booking = bookingsRepo.save(...)' and then creates 'BookingSeats' rows per seat.
    - Returns 'new BookingResponse(booking.getId(), booking.getTotalPrice(), booking.getBookingStatus())'.

- DiscountFactory
  - Method used: 'double applyDiscount(double basePrice, int ticketCount, Shows show)' (inferred from usage).
  - Composes 'DiscountStrategy' beans.

- Repositories (Spring Data JPA contracts)
  - SeatsRepo
    - List<Seats> findByShows_IdAndSeatNumberIn(Long showsId, List<String> seatNumbers)
  - ShowsRepo
    - Optional<Shows> findById(Long id)
    - List<Shows> findByTheatre_City(String city) (inferred)
  - BookingsRepo, BookingSeatsRepo, MovieRepo - standard CRUD methods; BookingSeatsRepo used to save mapping rows.

4. Entities (inferred fields and constraints)

- Seats
  - id: Long (PK)
  - seatNumber: String
  - status: SeatStatus (enum: AVAILABLE, BOOKED)
  - shows: Shows (ManyToOne) or shows_Id foreign key
  - @Version Integer version (optimistic locking)
  - Index/unique constraint: (shows_id, seat_number) UNIQUE

- Shows
  - id: Long
  - movie: Movie (FK)
  - theatre: Theatre (FK)
  - showTime: LocalDateTime
  - price: double

- Booking
  - id: Long
  - shows_Id: Long
  - ticketCount: int
  - totalPrice: double
  - bookingTime: LocalDateTime
  - bookingStatus: BookingStatus (enum)

- BookingSeats
  - id: Long
  - booking_Id: Long
  - seats_Id: Long

5. Detailed Booking Flow (pseudocode)

public BookingResponse bookTickets(BookingRequest req) {
  Shows show = showsRepo.findById(req.shows_Id()).orElseThrow(NotFound);
  List<Seats> seats = seatsRepo.findByShows_IdAndSeatNumberIn(req.shows_Id(), req.seats());
  if (seats.size() != req.seats().size()) throw BadRequest("Some seats not available");

  for (seat : seats) {
    if (seat.getStatus() != AVAILABLE) throw SeatAlreadyBookedException;
    seat.setStatus(BOOKED);
  }
  seatsRepo.saveAll(seats); // within same transaction

  basePrice = show.getPrice() * seats.size();
  finalPrice = discountFactory.applyDiscount(basePrice, seats.size(), show);

  Booking booking = new Booking(null, show.getId(), seats.size(), finalPrice, now(), CONFIRMED);
  booking = bookingsRepo.save(booking);

  for (seat : seats) {
    BookingSeats mapping = new BookingSeats();
    mapping.setBooking_Id(booking.getId());
    mapping.setSeats_Id(seat.getId());
    bookingSeatsRepo.save(mapping);
  }
  return new BookingResponse(booking.getId(), booking.getTotalPrice(), booking.getBookingStatus());
}

6. Error Handling & HTTP mapping

- Show not found -> RuntimeException("Show not found") -> recommend map to 404 NOT FOUND.
- Some seats missing -> RuntimeException("Some seats not available") -> recommend 400 BAD REQUEST or 409.
- SeatAlreadyBookedException -> currently handled by GlobalExceptionHandler -> HTTP 409 CONFLICT.
- DB optimistic lock failure -> OptimisticLockException -> transaction rollback; recommend retry with backoff (idempotency concerns).

7. Concurrency Strategies and Recommendations

Current state:
- Single `@Transactional` method encompassing read and write operations.
- Seats entity expected to have `@Version` for optimistic locking (see `LLD.md` earlier and codebase comments).

8. Tests to Add (priority)

- Unit tests for `BookingService.bookTickets` covering:
  - happy path (all seats available)
  - seat not found (size mismatch)
  - seat already booked (SeatAlreadyBookedException)
  - discount application (mock DiscountFactory)
- Integration tests with H2 / Testcontainers exercising transaction and optimistic locking behavior.
- Concurrency test: two parallel booking attempts for same seats asserting one succeeds and the other receives 409 or a retry success.

9. Observability & Metrics (low-level)

- Emit metrics from `BookingService`: booking_attempts_total, booking_success_total, booking_conflicts_total, optimistic_lock_retries_total.
- Add structured log entries around failure points with show id and seat numbers.

10. Deployment Notes

- Connection pool sizing: ensure DB pool > (expected concurrent booking threads per instance).
- Redis: configure TTL for seat locks slightly longer than expected booking time plus buffer.

11. File mappings (where to look in repo)

- Service: `src/main/java/com/example/interview_demo/service/BookingService.java`
- DTOs: `src/main/java/com/example/interview_demo/dto/BookingRequest.java`, `BookingResponse.java`
- Entities: `src/main/java/com/example/interview_demo/domain/entity` (Seats, Shows, Booking, BookingSeats)
- Repos: `src/main/java/com/example/interview_demo/repo`

12. Sample Requests / Responses

Request:
{
    "shows_Id":1,
    "seats": ["A1","A2"]
}

response: 200 ok
{
    "booking_Id": 5,
    "totalPrice": 400.0,
    "status": "CONFIRMED"
}

Conflict (409): Seat A1 is already booked.