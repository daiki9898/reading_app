package com.example.reading.validator;

import com.example.reading.service.CustomUserDetailsService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsernameValidator implements ConstraintValidator<Username, String>{
	
	private final CustomUserDetailsService userService;
	
	@Override
	public void initialize(Username annotaion) {
		
	}

	@Override
	public boolean isValid(String username, ConstraintValidatorContext context) {
		if (userService.isUsernameExists(username) == true) {
			return false;
		}
		return true;
	}

}
