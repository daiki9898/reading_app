package com.example.reading.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.reading.model.FinishedListRegistration;

public interface FinishedRegistrationRepository extends JpaRepository<FinishedListRegistration, Integer> {
	
	Optional<FinishedListRegistration> findByBookId(Integer bookId);
	
	void deleteByBookId(Integer bookId);
}

