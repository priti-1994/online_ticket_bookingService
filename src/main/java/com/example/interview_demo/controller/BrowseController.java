package com.example.interview_demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.interview_demo.domain.entity.Movie;
import com.example.interview_demo.domain.entity.Shows;
import com.example.interview_demo.service.BrowseService;

@RestController
@RequestMapping("/browse")
public class BrowseController {
	
	private final BrowseService browseService;

	public BrowseController(BrowseService browseService) {
		this.browseService = browseService;
	}
	
	@GetMapping("/language/{language}")
	public ResponseEntity<?> byLanguage(@PathVariable String language){
		List<Movie> movies = browseService.browseByLanguage(language);

	    if (movies.isEmpty()) {
	        String message = "Movies in language '" + language + "' are not available.";
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
	    }

	    return ResponseEntity.ok(movies);
	}

	@GetMapping("/genre/{genre}")
	public ResponseEntity<?> byGenre(@PathVariable String genre){
		List<Movie> movies = browseService.browseByGenre(genre);
		if (movies.isEmpty()) {
	        String message = "Movies in genre '" + genre + "' are not available.";
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
	    }

	    return ResponseEntity.ok(movies);
	}
	
	@GetMapping("/city/{city}")
	public ResponseEntity<?> byCity(@PathVariable String city){
		List<Shows> shows = browseService.browseShowsByCity(city);
		if (shows.isEmpty()) {
	        String message = "Shows in city '" + city + "' are not available.";
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
	    }

	    return ResponseEntity.ok(shows);
	}
}
