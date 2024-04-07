package com.example.reading.input;

import com.example.reading.validator.UnlinkedUserEmail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailInput {
	
	@Email(message = "不正な形式です")
	@UnlinkedUserEmail
	private String userEmail;
	
	@Pattern(regexp = "^.{8,20}$", message = "8〜20文字で入力してください")
	private String secretWord;

}
