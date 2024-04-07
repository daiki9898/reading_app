package com.example.reading.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.reading.model.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
	
	@Query(value = "SELECT generated_time FROM password_reset_tokens WHERE onetime_token = ?1", nativeQuery = true)
	Optional<LocalDateTime> getGeneratedTimeByToken(String token);
	
	@Query(value = "SELECT user_id FROM password_reset_tokens WHERE onetime_token = ?1", nativeQuery = true)
	Optional<Integer> getUserIdByToken(String token);
	
	@Query(value = "SELECT * FROM password_reset_tokens WHERE user_id = ?1 AND secret_word = ?2", nativeQuery = true)
	Optional<PasswordResetToken> existsByUserIdAndSecretWord(Integer userId, String secretWord);
	
//	@Modifying
//	@Query(value = "DELETE FROM password_reset_tokens WHERE onetime_token = ?1", nativeQuery = true)
	void deleteByOnetimeToken(String onetimeToken);

}
