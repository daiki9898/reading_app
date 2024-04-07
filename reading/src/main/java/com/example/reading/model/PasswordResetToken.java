package com.example.reading.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="password_reset_tokens")
public class PasswordResetToken {
	
	@Id
	@Column(name = "user_id")
	private Integer userId;
	
	@Column(name = "onetime_token")
	private String onetimeToken;
	
	@Column(name = "generated_time")
	private LocalDateTime generatedTime;
	
	@Column(name = "secret_word")
	private String secretWord;

}