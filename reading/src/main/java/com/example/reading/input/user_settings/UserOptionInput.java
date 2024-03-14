package com.example.reading.input.user_settings;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserOptionInput {
	
	@NotNull(message="IDがNullになっています")
	private Integer userId;
	
	private String genreTagStatus;
	
}
