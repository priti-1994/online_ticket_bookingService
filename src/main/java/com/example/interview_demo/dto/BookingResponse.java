package com.example.interview_demo.dto;

import com.example.interview_demo.domain.entity.BookingStatus;

public record BookingResponse(Long booking_Id, 
		double totalPrice, 
		BookingStatus status) {

}
