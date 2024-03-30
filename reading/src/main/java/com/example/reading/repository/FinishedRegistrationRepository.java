package com.example.reading.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.reading.model.FinishedListRegistration;

@Repository
public interface FinishedRegistrationRepository extends JpaRepository<FinishedListRegistration, Integer> {
	
	Optional<FinishedListRegistration> findByBookId(Integer bookId);
	
	@Query(value = "SELECT * from finished_list_registration WHERE user_id = ?1", nativeQuery = true)
	Optional<List<FinishedListRegistration>> getListByUserId(Integer userId);
	
	@Modifying
	@Query(value = "DELETE from finsihed_list_registration WHERE user_id = ?1", nativeQuery = true)
	void deleteByUserId(Integer userId);
	
	void deleteByBookId(Integer bookId);
}

