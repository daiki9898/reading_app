package com.example.reading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.reading.model.UserStatus;

public interface UserStatusRepository extends JpaRepository<UserStatus, Integer>{

	@Modifying
	@Query(value = "INSERT INTO user_status (user_id) VALUES (?1)", nativeQuery = true)
	void customSave(Integer userId);
	
	@Modifying
	@Query(value = "UPDATE user_status SET uploaded_image_number = ?1, total_image_size = ?2 WHERE user_id =?3", nativeQuery = true)
	void updateImageRegistrationStatus(Integer userId, int imageNumber, long imageSize);
	
	@Modifying
	@Query(value = "UPDATE user_status SET genre_tag_open_status = ?1 WHERE user_id = ?2", nativeQuery = true)
	void updateGenreTagStatusById(boolean genreTagOpenStatus, Integer userId);
	
}
