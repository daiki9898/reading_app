package com.example.reading.input.password_reset;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordInput {
	
	@NotEmpty(message = "tokenが空になっています")
	private String token;
	
	@Pattern(regexp = "^[a-zA-Z0-9]{8,20}$",  message = "半角英数字8〜20文字で入力してください")
	private String password;

}
