package com.example.reading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.reading.model.UserStatus;

public interface UserStatusRepository extends JpaRepository<UserStatus, Integer>{

	@Modifying
	@Query(value = "INSERT INTO user_status (user_id) VALUES (?1)", nativeQuery = true)
	void customSave(Integer userId);
}
