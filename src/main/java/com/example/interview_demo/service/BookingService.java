package com.example.interview_demo.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.interview_demo.domain.discount.DiscountFactory;
import com.example.interview_demo.domain.discount.DiscountStrategy;
import com.example.interview_demo.domain.entity.Booking;
import com.example.interview_demo.domain.entity.BookingSeats;
import com.example.interview_demo.domain.entity.BookingStatus;
import com.example.interview_demo.domain.entity.SeatStatus;
import com.example.interview_demo.domain.entity.Seats;
import com.example.interview_demo.domain.entity.Shows;
import com.example.interview_demo.dto.BookingRequest;
import com.example.interview_demo.dto.BookingResponse;
import com.example.interview_demo.exception.SeatAlreadyBookedException;
import com.example.interview_demo.repo.BookingsRepo;
import com.example.interview_demo.repo.BookingSeatsRepo;
import com.example.interview_demo.repo.SeatsRepo;
import com.example.interview_demo.repo.ShowsRepo;

@Service
public class BookingService {
	
	private final BookingsRepo bookingsRepo;
	private final ShowsRepo showsRepo;
	private final SeatsRepo seatsRepo;
	private final BookingSeatsRepo bookingSeatsRepo;
	private final DiscountFactory discountFactory;

	public BookingService(BookingsRepo bookingsRepo, 
			ShowsRepo showsRepo,
			SeatsRepo seatsRepo,
			BookingSeatsRepo bookingSeatsRepo,
			DiscountFactory discountFactory) {
		this.bookingsRepo = bookingsRepo;
		this.showsRepo = showsRepo;
		this.seatsRepo = seatsRepo;
		this.bookingSeatsRepo = bookingSeatsRepo;
		this.discountFactory = discountFactory;
	}

	@Transactional
	public BookingResponse bookTickets(BookingRequest bookingRequest) {
		
		Shows show = showsRepo.findById(bookingRequest.shows_Id())
				.orElseThrow(()-> new RuntimeException("Show not found"));
		
		List<Seats> seats = seatsRepo.findByShows_IdAndSeatNumberIn(
				bookingRequest.shows_Id(),
				bookingRequest.seats());
		
		if(seats.size() != bookingRequest.seats().size()) {
			throw new RuntimeException("Some seats not available");
		}
	
		for(Seats seat : seats) {
			if(seat.getStatus() != SeatStatus.AVAILABLE)
				throw new SeatAlreadyBookedException("Seat " + seat.getSeatNumber() + 
						" is already booked. Please choose another seat.");
			
//			if(!seatLockService.lockSeat(show.getId(), seat.getSeatNumber())) {
//				throw new RuntimeException("Seat locked");
//			}
			seat.setStatus(SeatStatus.BOOKED);
		}
		
		seatsRepo.saveAll(seats);
		
		int ticketCount = seats.size();
		double basePrice = show.getPrice() * ticketCount; 
		double finalPrice = discountFactory.applyDiscount(
				basePrice, 
				seats.size(),
				show);
		
		Booking booking = new Booking(
				null,
				show.getId(),
				ticketCount,
				finalPrice, 
				LocalDateTime.now(),
				BookingStatus.CONFIRMED
				);
		
		booking = bookingsRepo.save(booking);
		
		for(Seats seat : seats) {			
			BookingSeats bookingSeats = new BookingSeats();
			bookingSeats.setBooking_Id(booking.getId());
			bookingSeats.setSeats_Id(seat.getId());
			bookingSeatsRepo.save(bookingSeats);	
		}

		return new BookingResponse(
				booking.getId(),
				booking.getTotalPrice(), 
				booking.getBookingStatus());
	}
}
