package com.example.interview_demo.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Booking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long shows_Id;
	
	private int ticketCount;
	
	private double totalPrice;
	
	private LocalDateTime bookingTime;
	
	@Enumerated(EnumType.STRING)
	private BookingStatus bookingStatus;
	
	public Booking() {}

	public Booking(Long id, Long shows_Id, int ticketCount, double totalPrice, LocalDateTime bookingTime,
			BookingStatus bookingStatus) {
		this.id = id;
		this.shows_Id = shows_Id;
		this.ticketCount = ticketCount;
		this.totalPrice = totalPrice;
		this.bookingTime = bookingTime;
		this.bookingStatus = bookingStatus;
	}

	public Long getId() {
		return id;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public BookingStatus getBookingStatus() {
		return bookingStatus;
	}

	public void setBookingStatus(BookingStatus bookingStatus) {
		this.bookingStatus = bookingStatus;
	}

}
