package com.example.reading.input.user_settings;

import com.example.reading.validator.UserEmail;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserEmailInput {

	@Email(message = "不正な形式です")
	@UserEmail
	private String emailAddress;
}
