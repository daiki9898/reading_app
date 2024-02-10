package com.example.reading.input;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRegistrationInput {
	
	@Pattern(regexp = "^[a-zA-Z0-9]{8,20}$",  message = "半角英数字8〜20文字で入力してください")
	private String username;
	
	@Pattern(regexp = "^[a-zA-Z0-9]{8,20}$",  message = "半角英数字8〜20文字で入力してください")
	private String password;
}
