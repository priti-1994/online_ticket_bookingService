package com.example.interview_demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.interview_demo.domain.entity.Shows;

public interface ShowsRepo extends JpaRepository<Shows, Long> {
	List<Shows> findByTheatreCity(String city);
}
