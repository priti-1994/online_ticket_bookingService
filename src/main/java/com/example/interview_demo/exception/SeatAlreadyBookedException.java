package com.example.interview_demo.exception;

public class SeatAlreadyBookedException extends RuntimeException{
	
	public SeatAlreadyBookedException(String message) {
		super(message);
	}
}
