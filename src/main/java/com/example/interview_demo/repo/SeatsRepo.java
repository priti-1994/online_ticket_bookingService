package com.example.interview_demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.interview_demo.domain.entity.Seats;

public interface SeatsRepo extends JpaRepository<Seats, Long>{
	
	List<Seats> findByShows_IdAndSeatNumberIn(
			Long shows_Id,
			List<String> seatNumbers);

}
