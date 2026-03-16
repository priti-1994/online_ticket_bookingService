package com.example.interview_demo.domain.discount;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.interview_demo.domain.entity.Shows;

@Component
public class DiscountFactory {
	
	private final List<DiscountStrategy> strategies;
	
	public DiscountFactory(List<DiscountStrategy> strategies) {
		this.strategies = strategies;
	}

	public double applyDiscount(double price, int tickets, Shows shows){
		
		double result = price;
		
		for(DiscountStrategy s : strategies) {
			result = s.apply(result, tickets, shows);
		}
		
		return result;
	}

}
