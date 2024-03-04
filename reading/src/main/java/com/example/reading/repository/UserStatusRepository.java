package com.example.reading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.reading.model.UserStatus;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, Integer>{

	@Modifying
	@Query(value = "INSERT INTO user_status (user_id) VALUES (?1)", nativeQuery = true)
	public void customSave(Integer userId);
}
