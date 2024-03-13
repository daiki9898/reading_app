package com.example.reading.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.reading.model.ReadingListRegistration;

public interface ReadingRegistrationRepository extends JpaRepository<ReadingListRegistration, Integer>{

	Optional<ReadingListRegistration> findByBookId(Integer bookId);
	
	@Query(value = "SELECT * from reading_list_registration WHERE user_id = ?1", nativeQuery = true)
	Optional<List<ReadingListRegistration>> getListByUserId(Integer userId);
	
	@Modifying
	@Query(value = "DELETE from reading_list_registration WHERE user_id = ?1", nativeQuery = true)
	void deleteByUserId(Integer userId);
	
	@Modifying
	void deleteByBookId(Integer bookId);
	
}
