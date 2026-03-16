package com.example.interview_demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.interview_demo.domain.entity.Booking;

public interface BookingsRepo extends JpaRepository<Booking, Long>{}
