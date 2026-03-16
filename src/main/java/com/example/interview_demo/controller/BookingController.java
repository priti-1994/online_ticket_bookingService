package com.example.interview_demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.interview_demo.dto.BookingRequest;
import com.example.interview_demo.dto.BookingResponse;
import com.example.interview_demo.service.BookingService;

@RestController
@RequestMapping("/bookings")
public class BookingController {
	
	private final BookingService bookingService;
	
	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}
	
	@PostMapping
	public BookingResponse bookTickets(@RequestBody BookingRequest bookingRequest) {
		return bookingService.bookTickets(bookingRequest);
	}

}
