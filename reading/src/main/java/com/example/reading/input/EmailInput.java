package com.example.reading.input;

import com.example.reading.validator.UnlinkedUserEmail;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailInput {
	
	@Email(message = "不正な形式です")
	@UnlinkedUserEmail
	private String userEmail;

}
