package com.example.interview_demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.interview_demo.domain.entity.Movie;
import com.example.interview_demo.domain.entity.Shows;
import com.example.interview_demo.repo.MovieRepo;
import com.example.interview_demo.repo.ShowsRepo;

@Service
public class BrowseService {

	private final MovieRepo movieRepo;
	private final ShowsRepo showRepo;
	
	public BrowseService(MovieRepo movieRepo, ShowsRepo showRepo) {
		this.movieRepo = movieRepo;
		this.showRepo = showRepo;
	}
	
	public List<Movie> browseByLanguage(String language){
		return movieRepo.findByLanguage(language);
	}
	
	public List<Movie> browseByGenre(String genre){
		return movieRepo.findByGenre(genre);
	}
	
	public List<Shows> browseShowsByCity(String city){
		return showRepo.findByTheatreCity(city);
	}
}
