package com.example.interview_demo.domain.discount;


import org.springframework.stereotype.Component;

import com.example.interview_demo.domain.entity.Shows;

@Component
public class AfternoonDiscount implements DiscountStrategy{

	@Override
	public double apply(double price, int tickets, Shows shows) {
		// TODO Auto-generated method stub
		if(shows.getShowTime().getHour() > 13) {
			return price * 0.8;
		}
		return price;
	}

}
