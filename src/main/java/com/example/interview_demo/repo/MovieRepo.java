package com.example.interview_demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.interview_demo.domain.entity.Movie;

public interface MovieRepo extends JpaRepository<Movie, Long>{
	List<Movie> findByLanguage(String language);
	List<Movie> findByGenre(String genre);

}
