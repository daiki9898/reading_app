package com.example.reading.validator;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MaxFileSizeValidator implements ConstraintValidator<MaxFileSize, MultipartFile> {
	
	private final long MAX_SIZE_BYTES = 1024 * 1024; // 1MB
	
	@Override
	public void initialize(MaxFileSize annotaion) {
		
	}

	@Override
	public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
		if (file == null) {
			return true;
		}
		return file.getSize() <= MAX_SIZE_BYTES;
	}

}
