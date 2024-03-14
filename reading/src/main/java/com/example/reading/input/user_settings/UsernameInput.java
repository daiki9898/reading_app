package com.example.reading.input.user_settings;

import com.example.reading.validator.Username;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UsernameInput {

	@Pattern(regexp = "^[a-zA-Z0-9]{8,20}$",  message = "半角英数字8〜20文字で入力してください")
	@Username
	private String username;
}
