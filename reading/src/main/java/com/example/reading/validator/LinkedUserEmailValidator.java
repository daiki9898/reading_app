package com.example.reading.validator;

import com.example.reading.service.CustomUserDetailsService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LinkedUserEmailValidator implements ConstraintValidator<LinkedUserEmail, String>{
	
	private final CustomUserDetailsService userService;
	
	@Override
	public void initialize(LinkedUserEmail annotaion) {
		
	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		if (userService.isUserEmailExists(email) == true) {
			return false;
		}
		return true;
	}
}
