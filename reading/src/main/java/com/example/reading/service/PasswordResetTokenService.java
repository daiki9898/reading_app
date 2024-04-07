package com.example.reading.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reading.model.PasswordResetToken;
import com.example.reading.repository.PasswordResetTokenRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetTokenService {
	
	private final PasswordResetTokenRepository passwordResetTokenRepository;
	
//	public PasswordResetToken findById(Integer userId) {
//		return passwordResetTokenRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("登録情報は見つかりませんでした"));
//	}
	
	public boolean isValidOnetimeToken(String token) {
		 Optional<LocalDateTime> generatedTimeOptional = passwordResetTokenRepository.getGeneratedTimeByToken(token);
		 if (generatedTimeOptional.isPresent()) {
			 LocalDateTime generatedTime = generatedTimeOptional.get();
			 LocalDateTime currentTime = LocalDateTime.now();
			 return currentTime.isBefore(generatedTime.plusMinutes(3));
			 
		 } else {
			 return false;
		 }
	}
	
	public boolean isPasswordResetAllowed(Integer userId, String secretWord) {
		Optional<PasswordResetToken> token = passwordResetTokenRepository.existsByUserIdAndSecretWord(userId, secretWord);
		return token.isPresent();
	}
	
	public Integer getUserIdByToken(String token) {
		Integer userId = passwordResetTokenRepository.getUserIdByToken(token).orElseThrow(() -> new EntityNotFoundException("登録情報は見つかりませんでした"));
		return userId;
	}
	
	public void deleteByOnetimeToken(String token) {
		passwordResetTokenRepository.deleteByOnetimeToken(token);
	}

}
