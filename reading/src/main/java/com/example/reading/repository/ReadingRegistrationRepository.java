package com.example.reading.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.reading.model.ReadingListRegistration;

public interface ReadingRegistrationRepository extends JpaRepository<ReadingListRegistration, Integer>{

	Optional<ReadingListRegistration> findByBookId(Integer bookId);
	
	void deleteByBookId(Integer bookId);
}
