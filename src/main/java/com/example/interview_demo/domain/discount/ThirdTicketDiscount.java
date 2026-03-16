package com.example.interview_demo.domain.discount;

import org.springframework.stereotype.Component;

import com.example.interview_demo.domain.entity.Shows;

@Component
public class ThirdTicketDiscount implements DiscountStrategy{

	@Override
	public double apply(double price, int tickets, Shows shows) {
		// TODO Auto-generated method stub
		if(tickets >=3) {
			double single = price/tickets;
			return price - (single * 0.5);
		}
		return price;
	}

}
