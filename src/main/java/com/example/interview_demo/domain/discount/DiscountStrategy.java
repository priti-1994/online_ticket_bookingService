package com.example.interview_demo.domain.discount;

import com.example.interview_demo.domain.entity.Shows;

public interface DiscountStrategy {
	double apply(double price, int tickets, Shows shows);

}
