package com.example.reading.validator;

import com.example.reading.service.CustomUserDetailsService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnlinkedUserEmailValidator implements ConstraintValidator<UnlinkedUserEmail, String>{
	
	private final CustomUserDetailsService userService;
	
	@Override
	public void initialize(UnlinkedUserEmail annotaion) {
		
	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		if (userService.isUserEmailExists(email) == false) {
			return false;
		}
		return true;
	}
}
