package com.example.reading.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.reading.model.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {
	
}

