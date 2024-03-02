package com.example.reading.dto;

import lombok.Data;

@Data
public class UserDto {
	
	private String username;
	
	private int readingBookNumber;
	
	private int finishedBookNumber;
	
	private int uploadedImageNumber;
	
	private long totalImageSize;
	
	private String registeredDate;
	
	private String loginDuration;
}
