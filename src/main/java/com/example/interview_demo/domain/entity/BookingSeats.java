package com.example.interview_demo.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class BookingSeats {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long booking_Id;
	
	private Long seats_Id;
	
	public BookingSeats() {}

	public BookingSeats(Long booking_Id, Long seats_Id) {
		this.booking_Id = booking_Id;
		this.seats_Id = seats_Id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBooking_Id() {
		return booking_Id;
	}

	public void setBooking_Id(Long booking_Id) {
		this.booking_Id = booking_Id;
	}

	public Long getSeats_Id() {
		return seats_Id;
	}

	public void setSeats_Id(Long seats_Id) {
		this.seats_Id = seats_Id;
	}
	
	

	

}
