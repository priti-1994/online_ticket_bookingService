package com.example.interview_demo.dto;

import java.util.List;

public record BookingRequest(		
		Long shows_Id,
		List<String> seats) {

}
