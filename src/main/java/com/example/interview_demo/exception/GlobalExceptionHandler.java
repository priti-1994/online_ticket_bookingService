package com.example.interview_demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(SeatAlreadyBookedException.class)
	public ResponseEntity<String> handleAlreadyBookedSeats(SeatAlreadyBookedException ex){
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ex.getMessage());
	}
}