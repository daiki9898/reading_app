package com.example.reading.validator;

import com.example.reading.service.CustomUserDetailsService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserEmailValidator implements ConstraintValidator<UserEmail, String>{
	
	private final CustomUserDetailsService userService;
	
	@Override
	public void initialize(UserEmail annotaion) {
		
	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		if (userService.isUserEmailExists(email) == true) {
			return false;
		}
		return true;
	}
}
