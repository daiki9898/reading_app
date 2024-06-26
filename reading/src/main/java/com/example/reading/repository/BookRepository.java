package com.example.reading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.reading.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
	
}

