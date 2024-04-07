package com.example.reading.input.password_reset;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PasswordResetVerificationInput {
	
	@NotEmpty(message = "tokenが空になっています")
	private String token;
	
	private String username;
	
	private String secretWord;

}
