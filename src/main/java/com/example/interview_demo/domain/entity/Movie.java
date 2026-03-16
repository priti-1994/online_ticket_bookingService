package com.example.interview_demo.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Movie {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String language;
	private String genre;
	
	public Movie() {}

	public Movie(Long id, String name, String language, String genre) {
		this.id = id;
		this.name = name;
		this.language = language;
		this.genre = genre;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLanguage() {
		return language;
	}

	public String getGenre() {
		return genre;
	}

}
